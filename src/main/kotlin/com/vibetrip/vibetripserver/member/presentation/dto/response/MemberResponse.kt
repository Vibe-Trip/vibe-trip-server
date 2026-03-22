package com.vibetrip.vibetripserver.member.presentation.dto.response

data class MemberResponse(
    val name: String,
    val email: String,
    val profileImage: String,
    val albumCount: Long,
    val albumLogCount: Long,
)
