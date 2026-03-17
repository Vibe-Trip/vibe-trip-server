package com.vibetrip.vibetripserver.auth.business

import com.vibetrip.vibetripserver.auth.domain.Jwt
import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.auth.domain.TokenType
import com.vibetrip.vibetripserver.auth.implement.*
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.stereotype.Service

@Service
class OAuthService(
    authenticators: List<OAuthAuthenticator>,
    private val oAuthRegistrar: OAuthRegistrar,
    private val jwtGenerator: JwtGenerator,
    private val jwtValidator: JwtValidator,
    private val refreshTokenManager: RefreshTokenManager,
) {

    private val oauthAuthenticatorMap: Map<OAuthProvider, OAuthAuthenticator> =
        authenticators.associateBy { it.provider }

    fun login(newOAuthLogin: NewOAuthLogin): Jwt {
        val oAuthMember = oauthAuthenticatorMap[newOAuthLogin.provider]?.authenticate(newOAuthLogin.authToken)
            ?: throw AppException(ErrorType.SERVER_ERROR)

        val memberKey = oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember)

        return jwtGenerator.generateJwt(memberKey)
    }

    fun refresh(refreshToken: String): Jwt {
        val memberKey = jwtValidator.getSubjectIfValidWithType(refreshToken, TokenType.REFRESH)

        val savedRefreshToken = refreshTokenManager.findByMemberKey(memberKey)

        savedRefreshToken.validateReuse(refreshToken) {
            logger.warn("[Token Reuse Detected]: memberKey=$memberKey | 토큰 탈취 가능성으로 세션 무효화")
            refreshTokenManager.delete(id)
        }

        return jwtGenerator.generateJwt(memberKey).also { jwt ->
            refreshTokenManager.update(savedRefreshToken.id, jwt.refreshToken)
        }
    }
}