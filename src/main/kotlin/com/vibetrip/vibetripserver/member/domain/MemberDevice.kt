package com.vibetrip.vibetripserver.member.domain

data class MemberDevice(
    val deviceId: String,
    val fcmToken: String,
    val memberKey: String,
)
