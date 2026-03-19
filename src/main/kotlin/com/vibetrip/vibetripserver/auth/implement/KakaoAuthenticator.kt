package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.domain.KakaoUser
import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class KakaoAuthenticator(
    private val restClient: RestClient,
) : OAuthAuthenticator {
    companion object {
        private const val KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me"
        private const val BEARER = "Bearer "
    }

    override val provider = OAuthProvider.KAKAO

    override fun authenticate(newOAuthLogin: NewOAuthLogin) =
        runCatching {
            restClient
                .get()
                .uri(KAKAO_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "$BEARER${newOAuthLogin.authToken}")
                .retrieve()
                .body<KakaoUser>()
        }.getOrNull()
            ?.toOAuthUser()
            ?: throw AppException(ErrorType.INVALID_OAUTH_USER)
}
