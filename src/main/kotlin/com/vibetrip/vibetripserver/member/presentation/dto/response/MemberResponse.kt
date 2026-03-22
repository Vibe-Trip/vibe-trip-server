package com.vibetrip.vibetripserver.member.presentation.dto.response

data class MemberResponse (
    val name: String,
    val email: String,
    val profilImage : String,
    val albumCount: Int,
    val albumLogCount: Int,
)