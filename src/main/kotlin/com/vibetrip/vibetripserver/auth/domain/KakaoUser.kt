package com.vibetrip.vibetripserver.auth.domain

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoUser(
    val id: Long,
    val connectedAt: LocalDateTime,
    val kakaoAccount: KakaoAccount,
) {
    fun toOAuthUser() =
        OAuthMember.of(
            account = id.toString(),
            provider = OAuthProvider.KAKAO,
            name = kakaoAccount.profile.nickname,
            email = kakaoAccount.email,
            profileImageUrl = kakaoAccount.profile.profileImageUrl
        )
}
