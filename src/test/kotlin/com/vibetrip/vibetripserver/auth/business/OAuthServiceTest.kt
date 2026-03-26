package com.vibetrip.vibetripserver.auth.business

import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.auth.domain.TokenType
import com.vibetrip.vibetripserver.auth.implement.JwtGenerator
import com.vibetrip.vibetripserver.auth.implement.JwtValidator
import com.vibetrip.vibetripserver.auth.implement.OAuthAuthenticator
import com.vibetrip.vibetripserver.auth.implement.OAuthRegistrar
import com.vibetrip.vibetripserver.auth.implement.RefreshTokenManager
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.fixture.AuthFixture
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OAuthServiceTest {
    private val kakaoAuthenticator = mockk<OAuthAuthenticator>()
    private val appleAuthenticator = mockk<OAuthAuthenticator>()
    private val oAuthRegistrar = mockk<OAuthRegistrar>()
    private val jwtGenerator = mockk<JwtGenerator>()
    private val jwtValidator = mockk<JwtValidator>()
    private val refreshTokenManager = mockk<RefreshTokenManager>()

    private lateinit var oAuthService: OAuthService

    @BeforeEach
    fun setUp() {
        every { kakaoAuthenticator.provider } returns OAuthProvider.KAKAO
        every { appleAuthenticator.provider } returns OAuthProvider.APPLE

        oAuthService =
            OAuthService(
                authenticators = listOf(kakaoAuthenticator, appleAuthenticator),
                oAuthRegistrar = oAuthRegistrar,
                jwtGenerator = jwtGenerator,
                jwtValidator = jwtValidator,
                refreshTokenManager = refreshTokenManager,
            )
    }

    @Test
    fun `Kakao 로그인 시 유저 정보 조회가 성공하면 JWT가 반환된다`() {
        // given
        val kakaoLogin = AuthFixture.kakaoLogin()
        val oAuthMember = AuthFixture.kakaoMember()
        val memberKey = "member-key-123"
        val expectedJwt = AuthFixture.jwt()

        every { kakaoAuthenticator.authenticate(kakaoLogin) } returns oAuthMember
        every { oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember, kakaoLogin.fcmToken, kakaoLogin.deviceId) } returns memberKey
        every { jwtGenerator.generateJwt(memberKey) } returns expectedJwt
        every { refreshTokenManager.update(expectedJwt.refreshToken, memberKey) } returns Unit

        // when
        val result = oAuthService.login(kakaoLogin)

        // then
        assertThat(result).isEqualTo(expectedJwt)
        verify { kakaoAuthenticator.authenticate(kakaoLogin) }
        verify { oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember, kakaoLogin.fcmToken, kakaoLogin.deviceId) }
        verify { jwtGenerator.generateJwt(memberKey) }
        verify { refreshTokenManager.update(expectedJwt.refreshToken, memberKey) }
    }

    @Test
    fun `Kakao 로그인 시 유저 정보 조회 값이 없으면 INVALID_OAUTH_USER 예외가 발생한다`() {
        // given
        val kakaoLogin = AuthFixture.kakaoLogin()

        every { kakaoAuthenticator.authenticate(kakaoLogin) } throws AppException(ErrorType.INVALID_OAUTH_USER)

        // when & then
        val exception =
            assertThrows<AppException> {
                oAuthService.login(kakaoLogin)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.INVALID_OAUTH_USER)
    }

    @Test
    fun `Apple 로그인 시 유저 정보 조회가 성공하면 JWT가 반환된다`() {
        // given
        val appleLogin = AuthFixture.appleLogin()
        val oAuthMember = AuthFixture.appleMember()
        val memberKey = "member-key-456"
        val expectedJwt = AuthFixture.jwt()

        every { appleAuthenticator.authenticate(appleLogin) } returns oAuthMember
        every { oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember, appleLogin.fcmToken, appleLogin.deviceId) } returns memberKey
        every { jwtGenerator.generateJwt(memberKey) } returns expectedJwt
        every { refreshTokenManager.update(expectedJwt.refreshToken, memberKey) } returns Unit

        // when
        val result = oAuthService.login(appleLogin)

        // then
        assertThat(result).isEqualTo(expectedJwt)
    }

    @Test
    fun `Apple 로그인 시 Identity Token이 유효하지 않으면 INVALID_APPLE_IDENTITY_TOKEN 예외가 발생한다`() {
        // given
        val appleLogin = AuthFixture.appleLogin()

        every { appleAuthenticator.authenticate(appleLogin) } throws AppException(ErrorType.INVALID_APPLE_IDENTITY_TOKEN)

        // when & then
        val exception =
            assertThrows<AppException> {
                oAuthService.login(appleLogin)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.INVALID_APPLE_IDENTITY_TOKEN)
    }

    @Test
    fun `토큰 갱신 시 유효한 리프레시 토큰이면 새로운 JWT가 반환된다`() {
        // given
        val bearerRefreshToken = "Bearer valid-refresh-token"
        val tokenBody = "valid-refresh-token"
        val memberKey = "member-key-123"
        val savedRefreshToken =
            AuthFixture.refreshToken(
                refreshToken = tokenBody,
                memberKey = memberKey,
            )
        val newJwt =
            AuthFixture.jwt(
                accessToken = "new-access-token",
                refreshToken = "new-refresh-token",
            )

        every { jwtValidator.getBearerTokenBody(bearerRefreshToken) } returns tokenBody
        every { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) } returns memberKey
        every { refreshTokenManager.findByMemberKey(memberKey) } returns savedRefreshToken
        every { jwtGenerator.generateJwt(memberKey) } returns newJwt
        every { refreshTokenManager.update(newJwt.refreshToken, memberKey) } returns Unit

        // when
        val result = oAuthService.refresh(bearerRefreshToken)

        // then
        assertThat(result).isEqualTo(newJwt)
        verify { jwtValidator.getBearerTokenBody(bearerRefreshToken) }
        verify { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) }
        verify { refreshTokenManager.update(newJwt.refreshToken, memberKey) }
    }

    @Test
    fun `토큰 갱신 시 리프레시 토큰이 재사용되면 FAILED_AUTH 예외가 발생하고 토큰이 삭제된다`() {
        // given
        val bearerRefreshToken = "Bearer valid-refresh-token"
        val tokenBody = "valid-refresh-token"
        val memberKey = "member-key-123"
        val savedRefreshToken =
            AuthFixture.refreshToken(
                refreshToken = "different-refresh-token",
                memberKey = memberKey,
            )

        every { jwtValidator.getBearerTokenBody(bearerRefreshToken) } returns tokenBody
        every { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) } returns memberKey
        every { refreshTokenManager.findByMemberKey(memberKey) } returns savedRefreshToken
        every { refreshTokenManager.delete(savedRefreshToken.id) } returns Unit

        // when & then
        val exception =
            assertThrows<AppException> {
                oAuthService.refresh(bearerRefreshToken)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.FAILED_AUTH)
        verify { refreshTokenManager.delete(savedRefreshToken.id) }
    }

    @Test
    fun `토큰 갱신 시 저장된 리프레시 토큰이 없으면 NOT_FOUND_DATA 예외가 발생한다`() {
        // given
        val bearerRefreshToken = "Bearer valid-refresh-token"
        val tokenBody = "valid-refresh-token"
        val memberKey = "member-key-123"

        every { jwtValidator.getBearerTokenBody(bearerRefreshToken) } returns tokenBody
        every { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) } returns memberKey
        every { refreshTokenManager.findByMemberKey(memberKey) } throws AppException(ErrorType.NOT_FOUND_DATA)

        // when & then
        val exception =
            assertThrows<AppException> {
                oAuthService.refresh(bearerRefreshToken)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND_DATA)
    }

    @Test
    fun `토큰 갱신 시 리프레시 토큰이 만료되었으면 EXPIRED_JWT 예외가 발생한다`() {
        // given
        val bearerRefreshToken = "Bearer valid-refresh-token"
        val tokenBody = "valid-refresh-token"

        every { jwtValidator.getBearerTokenBody(bearerRefreshToken) } returns tokenBody
        every { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) } throws AppException(ErrorType.EXPIRED_JWT)

        // when & then
        val exception =
            assertThrows<AppException> {
                oAuthService.refresh(bearerRefreshToken)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.EXPIRED_JWT)
    }

    @Test
    fun `토큰 갱신 시 Bearer 토큰 형식이 아니면 INVALID_TOKEN_METHOD 예외가 발생한다`() {
        // given
        val invalidToken = "invalid-token"

        every { jwtValidator.getBearerTokenBody(invalidToken) } throws AppException(ErrorType.INVALID_TOKEN_METHOD)

        // when & then
        val exception =
            assertThrows<AppException> {
                oAuthService.refresh(invalidToken)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.INVALID_TOKEN_METHOD)
    }
}
