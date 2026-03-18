package com.vibetrip.vibetripserver.auth.presentation.dto.request

import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.member.domain.vo.IpAddress
import jakarta.validation.constraints.NotBlank

data class AppleLoginRequest(
    @field:NotBlank(message = "인증 토큰 값은 필수입니다.")
    val identityToken: String,
    @field:NotBlank(message = "FCM Token은 필수입니다.")
    val fcmToken: String,
    @field:NotBlank(message = "Device ID는 필수입니다.")
    val deviceId: String,
    val name: String?,
) {
    fun toNewOAuthLogin(ipAddress: String) = NewOAuthLogin.Apple(
        authToken = identityToken,
        provider = OAuthProvider.APPLE,
        fcmToken = fcmToken,
        ipAddress = IpAddress(ipAddress),
        deviceId = deviceId,
        name = name,
    )
}
