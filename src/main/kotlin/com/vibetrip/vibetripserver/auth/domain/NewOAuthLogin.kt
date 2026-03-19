package com.vibetrip.vibetripserver.auth.domain

import com.vibetrip.vibetripserver.member.domain.vo.IpAddress

sealed class NewOAuthLogin {
    abstract val authToken: String
    abstract val provider: OAuthProvider
    abstract val fcmToken: String
    abstract val deviceId: String
    abstract val ipAddress: IpAddress

    data class Kakao(
        override val authToken: String,
        override val provider: OAuthProvider,
        override val fcmToken: String,
        override val deviceId: String,
        override val ipAddress: IpAddress
    ) : NewOAuthLogin()

    data class Apple(
        override val authToken: String,
        override val provider: OAuthProvider,
        override val fcmToken: String,
        override val deviceId: String,
        override val ipAddress: IpAddress,
        val name: String? = null,
    ) : NewOAuthLogin()
}