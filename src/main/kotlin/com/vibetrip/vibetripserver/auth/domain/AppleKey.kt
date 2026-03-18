package com.vibetrip.vibetripserver.auth.domain

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AppleKey(
    val kty: String,
    val kid: String,
    val use: String,
    val alg: String,
    val n: String,
    val e: String,
) {
}