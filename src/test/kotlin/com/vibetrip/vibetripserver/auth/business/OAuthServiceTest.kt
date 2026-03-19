package com.vibetrip.vibetripserver.auth.business

import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.auth.domain.TokenType
import com.vibetrip.vibetripserver.auth.implement.*
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.fixture.AuthFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class OAuthServiceTest : BehaviorSpec(
    {
        val kakaoAuthenticator = mockk<OAuthAuthenticator>()
        val appleAuthenticator = mockk<OAuthAuthenticator>()
        val oAuthRegistrar = mockk<OAuthRegistrar>()
        val jwtGenerator = mockk<JwtGenerator>()
        val jwtValidator = mockk<JwtValidator>()
        val refreshTokenManager = mockk<RefreshTokenManager>()

        every { kakaoAuthenticator.provider } returns OAuthProvider.KAKAO
        every { appleAuthenticator.provider } returns OAuthProvider.APPLE

        val oAuthService = OAuthService(
            authenticators = listOf(kakaoAuthenticator, appleAuthenticator),
            oAuthRegistrar = oAuthRegistrar,
            jwtGenerator = jwtGenerator,
            jwtValidator = jwtValidator,
            refreshTokenManager = refreshTokenManager,
        )

        Given("OAuth Kakao Login을 하는 상황에서") {
            val kakaoLogin = AuthFixture.kakaoLogin()

            When("유저 정보 조회가 성공하면") {
                val oAuthMember = AuthFixture.kakaoMember()
                val memberKey = "member-key-123"
                val expectedJwt = AuthFixture.jwt()

                every { kakaoAuthenticator.authenticate(kakaoLogin) } returns oAuthMember
                every { oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember, kakaoLogin.fcmToken, kakaoLogin.deviceId) } returns memberKey
                every { jwtGenerator.generateJwt(memberKey) } returns expectedJwt
                every { refreshTokenManager.update(expectedJwt.refreshToken, memberKey) } returns Unit

                Then("JWT가 반환된다") {
                    val result = oAuthService.login(kakaoLogin)

                    result shouldBe expectedJwt
                    verify { kakaoAuthenticator.authenticate(kakaoLogin) }
                    verify { oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember, kakaoLogin.fcmToken, kakaoLogin.deviceId) }
                    verify { jwtGenerator.generateJwt(memberKey) }
                    verify { refreshTokenManager.update(expectedJwt.refreshToken, memberKey) }
                }
            }

            When("유저 정보 조회 값이 없으면") {
                every { kakaoAuthenticator.authenticate(kakaoLogin) } throws AppException(ErrorType.INVALID_OAUTH_USER)

                Then("AppException 예외가 발생한다") {
                    val exception = shouldThrow<AppException> {
                        oAuthService.login(kakaoLogin)
                    }

                    exception.errorType shouldBe ErrorType.INVALID_OAUTH_USER
                }
            }
        }

        Given("OAuth Apple Login을 하는 상황에서") {
            val appleLogin = AuthFixture.appleLogin()

            When("유저 정보 조회가 성공하면") {
                val oAuthMember = AuthFixture.appleMember()
                val memberKey = "member-key-456"
                val expectedJwt = AuthFixture.jwt()

                every { appleAuthenticator.authenticate(appleLogin) } returns oAuthMember
                every { oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember, appleLogin.fcmToken, appleLogin.deviceId) } returns memberKey
                every { jwtGenerator.generateJwt(memberKey) } returns expectedJwt
                every { refreshTokenManager.update(expectedJwt.refreshToken, memberKey) } returns Unit

                Then("JWT가 반환된다") {
                    val result = oAuthService.login(appleLogin)

                    result shouldBe expectedJwt
                }
            }

            When("Apple Identity Token이 유효하지 않으면") {
                every { appleAuthenticator.authenticate(appleLogin) } throws AppException(ErrorType.INVALID_APPLE_IDENTITY_TOKEN)

                Then("AppException 예외가 발생한다") {
                    val exception = shouldThrow<AppException> {
                        oAuthService.login(appleLogin)
                    }

                    exception.errorType shouldBe ErrorType.INVALID_APPLE_IDENTITY_TOKEN
                }
            }
        }

        Given("토큰 갱신을 하는 상황에서") {
            val bearerRefreshToken = "Bearer valid-refresh-token"
            val tokenBody = "valid-refresh-token"
            val memberKey = "member-key-123"

            When("유효한 리프레시 토큰이면") {
                val savedRefreshToken = AuthFixture.refreshToken(
                    refreshToken = tokenBody,
                    memberKey = memberKey,
                )
                val newJwt = AuthFixture.jwt(
                    accessToken = "new-access-token",
                    refreshToken = "new-refresh-token",
                )

                every { jwtValidator.getBearerTokenBody(bearerRefreshToken) } returns tokenBody
                every { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) } returns memberKey
                every { refreshTokenManager.findByMemberKey(memberKey) } returns savedRefreshToken
                every { jwtGenerator.generateJwt(memberKey) } returns newJwt
                every { refreshTokenManager.update(newJwt.refreshToken, memberKey) } returns Unit

                Then("새로운 JWT가 반환된다") {
                    val result = oAuthService.refresh(bearerRefreshToken)

                    result shouldBe newJwt
                    verify { jwtValidator.getBearerTokenBody(bearerRefreshToken) }
                    verify { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) }
                    verify { refreshTokenManager.update(newJwt.refreshToken, memberKey) }
                }
            }

            When("리프레시 토큰이 재사용되면") {
                val savedRefreshToken = AuthFixture.refreshToken(
                    refreshToken = "different-refresh-token",
                    memberKey = memberKey,
                )

                every { jwtValidator.getBearerTokenBody(bearerRefreshToken) } returns tokenBody
                every { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) } returns memberKey
                every { refreshTokenManager.findByMemberKey(memberKey) } returns savedRefreshToken
                every { refreshTokenManager.delete(savedRefreshToken.id) } returns Unit

                Then("AppException 예외가 발생하고 토큰이 삭제된다") {
                    val exception = shouldThrow<AppException> {
                        oAuthService.refresh(bearerRefreshToken)
                    }

                    exception.errorType shouldBe ErrorType.FAILED_AUTH
                    verify { refreshTokenManager.delete(savedRefreshToken.id) }
                }
            }

            When("저장된 리프레시 토큰이 없으면") {
                every { jwtValidator.getBearerTokenBody(bearerRefreshToken) } returns tokenBody
                every { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) } returns memberKey
                every { refreshTokenManager.findByMemberKey(memberKey) } throws AppException(ErrorType.NOT_FOUND_DATA)

                Then("AppException 예외가 발생한다") {
                    val exception = shouldThrow<AppException> {
                        oAuthService.refresh(bearerRefreshToken)
                    }

                    exception.errorType shouldBe ErrorType.NOT_FOUND_DATA
                }
            }

            When("리프레시 토큰이 만료되었으면") {
                every { jwtValidator.getBearerTokenBody(bearerRefreshToken) } returns tokenBody
                every { jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH) } throws AppException(ErrorType.EXPIRED_JWT)

                Then("AppException 예외가 발생한다") {
                    val exception = shouldThrow<AppException> {
                        oAuthService.refresh(bearerRefreshToken)
                    }

                    exception.errorType shouldBe ErrorType.EXPIRED_JWT
                }
            }

            When("Bearer 토큰 형식이 아니면") {
                val invalidToken = "invalid-token"

                every { jwtValidator.getBearerTokenBody(invalidToken) } throws AppException(ErrorType.INVALID_TOKEN_METHOD)

                Then("AppException 예외가 발생한다") {
                    val exception = shouldThrow<AppException> {
                        oAuthService.refresh(invalidToken)
                    }

                    exception.errorType shouldBe ErrorType.INVALID_TOKEN_METHOD
                }
            }
        }
    }
)
