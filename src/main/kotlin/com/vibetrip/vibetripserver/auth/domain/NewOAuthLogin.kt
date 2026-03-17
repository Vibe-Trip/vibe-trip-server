package com.vibetrip.vibetripserver.auth.domain

import com.vibetrip.vibetripserver.member.domain.vo.IpAddress

data class NewOAuthLogin(
    val authToken: String,
    val provider: OAuthProvider,
    val fcmToken: String,
    val ipAddress: IpAddress,
) {
}