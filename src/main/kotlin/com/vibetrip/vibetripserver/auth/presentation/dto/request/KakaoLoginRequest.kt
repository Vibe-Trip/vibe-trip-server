package com.vibetrip.vibetripserver.auth.presentation.dto.request

import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.member.domain.vo.IpAddress
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class KakaoLoginRequest(
    @field:NotBlank(message = "인증 토큰 값은 필수입니다.")
    val accessToken: String,
    @field:NotNull(message = "FCM Token은 Null일 수 없습니다.")
    val fcmToken: String,
    @field:NotBlank(message = "Device ID는 필수입니다.")
    val deviceId: String,
) {
    fun toNewOAuthLogin(ipAddress: String) =
        NewOAuthLogin.Kakao(
            authToken = accessToken,
            provider = OAuthProvider.KAKAO,
            fcmToken = fcmToken,
            deviceId = deviceId,
            ipAddress = IpAddress(ipAddress),
        )
}
