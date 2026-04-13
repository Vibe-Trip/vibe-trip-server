package com.vibetrip.vibetripserver.fixture

import com.vibetrip.vibetripserver.auth.domain.Jwt
import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthMember
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.auth.domain.RefreshToken
import com.vibetrip.vibetripserver.member.domain.vo.IpAddress

object AuthFixture {
    fun kakaoLogin(
        authToken: String = "kakao-access-token",
        fcmToken: String = "fcm-token",
        deviceId: String = "device-id",
        ipAddress: String = "127.0.0.1",
    ) = NewOAuthLogin.Kakao(
        authToken = authToken,
        provider = OAuthProvider.KAKAO,
        fcmToken = fcmToken,
        deviceId = deviceId,
        ipAddress = IpAddress(ipAddress),
    )

    fun appleLogin(
        authToken: String = "apple-identity-token",
        fcmToken: String = "fcm-token",
        deviceId: String = "device-id",
        ipAddress: String = "127.0.0.1",
        name: String? = "Apple User",
    ) = NewOAuthLogin.Apple(
        authToken = authToken,
        provider = OAuthProvider.APPLE,
        fcmToken = fcmToken,
        deviceId = deviceId,
        ipAddress = IpAddress(ipAddress),
        name = name,
    )

    fun kakaoMember(
        account: String = "12345",
        name: String = "홍길동",
        email: String = "test@kakao.com",
        profileImageUrl: String = "https://example.com/profile.jpg",
    ) = OAuthMember.of(
        account = account,
        provider = OAuthProvider.KAKAO,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
    )

    fun appleMember(
        account: String = "apple-sub-123",
        name: String = "Apple User",
        email: String = "test@privaterelay.appleid.com",
        profileImageUrl: String = "",
    ) = OAuthMember.of(
        account = account,
        provider = OAuthProvider.APPLE,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
    )

    fun jwt(
        accessToken: String = "Bearer access-token",
        refreshToken: String = "Bearer refresh-token",
    ) = Jwt(accessToken, refreshToken)

    fun refreshToken(
        id: Long = 1L,
        refreshToken: String = "refresh-token",
        memberKey: String = "member-key",
    ) = RefreshToken(
        id = id,
        refreshToken = refreshToken,
        memberKey = memberKey,
    )
}
