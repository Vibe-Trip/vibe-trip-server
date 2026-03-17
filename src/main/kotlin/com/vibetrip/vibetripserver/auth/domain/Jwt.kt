package com.vibetrip.vibetripserver.auth.domain

data class Jwt(
    val accessToken: String,
    val refreshToken: String,
)
