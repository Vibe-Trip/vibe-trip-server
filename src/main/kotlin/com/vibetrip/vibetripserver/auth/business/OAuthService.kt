package com.vibetrip.vibetripserver.auth.business

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.auth.domain.Jwt
import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.auth.implement.JwtGenerator
import com.vibetrip.vibetripserver.auth.implement.OAuthAuthenticator
import com.vibetrip.vibetripserver.auth.implement.OAuthRegistrar
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.stereotype.Service

@Service
class OAuthService(
    authenticators: List<OAuthAuthenticator>,
    private val oAuthRegistrar: OAuthRegistrar,
    private val jwtGenerator: JwtGenerator,
) {

    private val oauthAuthenticatorMap: Map<OAuthProvider, OAuthAuthenticator> =
        authenticators.associateBy { it.provider }

    fun login(newOAuthLogin: NewOAuthLogin): Jwt {
        val oAuthMember = oauthAuthenticatorMap[newOAuthLogin.provider]?.authenticate(newOAuthLogin.authToken)
            ?: throw AppException(ErrorType.SERVER_ERROR)

        val memberKey = oAuthRegistrar.registerIfNewAndGetMemberKey(oAuthMember)

        return jwtGenerator.generateJwt(memberKey)
    }
}