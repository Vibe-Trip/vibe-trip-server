package com.vibetrip.vibetripserver.auth.presentation.dto.response

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String,
)