package com.vibetrip.vibetripserver.auth.business

import com.vibetrip.vibetripserver.auth.domain.Jwt
import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.auth.domain.TokenType
import com.vibetrip.vibetripserver.auth.implement.JwtGenerator
import com.vibetrip.vibetripserver.auth.implement.JwtValidator
import com.vibetrip.vibetripserver.auth.implement.OAuthAuthenticator
import com.vibetrip.vibetripserver.auth.implement.OAuthRegistrar
import com.vibetrip.vibetripserver.auth.implement.RefreshTokenManager
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import com.vibetrip.vibetripserver.member.domain.MemberDevice
import com.vibetrip.vibetripserver.member.implement.MemberDeviceManager
import org.springframework.stereotype.Service

@Service
class OAuthService(
    authenticators: List<OAuthAuthenticator>,
    private val oAuthRegistrar: OAuthRegistrar,
    private val jwtGenerator: JwtGenerator,
    private val jwtValidator: JwtValidator,
    private val memberDeviceManager: MemberDeviceManager,
    private val refreshTokenManager: RefreshTokenManager,
) {
    private val oauthAuthenticatorMap: Map<OAuthProvider, OAuthAuthenticator> =
        authenticators.associateBy { it.provider }

    fun login(newOAuthLogin: NewOAuthLogin): Jwt {
        val oAuthMember =
            oauthAuthenticatorMap[newOAuthLogin.provider]?.authenticate(newOAuthLogin)
                ?: throw AppException(ErrorType.SERVER_ERROR)

        val memberKey =
            oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember).also {
                memberDeviceManager.saveOrUpdate(MemberDevice(newOAuthLogin.deviceId, newOAuthLogin.fcmToken, it))
            }

        return jwtGenerator.generateJwt(memberKey).also {
            refreshTokenManager.update(it.refreshToken, memberKey)
        }
    }

    fun refresh(refreshToken: String): Jwt {
        val tokenBody = jwtValidator.getBearerTokenBody(refreshToken)
        val memberKey = jwtValidator.getSubjectIfValidWithType(tokenBody, TokenType.REFRESH)

        val savedRefreshToken = refreshTokenManager.findByMemberKey(memberKey)

        savedRefreshToken.validateReuse(tokenBody) {
            logger.warn { "[Token Reuse Detected]: memberKey=$memberKey | 토큰 탈취 가능성으로 세션 무효화" }
            refreshTokenManager.delete(id)
        }

        return jwtGenerator.generateJwt(memberKey).also {
            refreshTokenManager.update(it.refreshToken, memberKey)
        }
    }
}
