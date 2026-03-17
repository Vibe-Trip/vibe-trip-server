package com.vibetrip.vibetripserver.auth.domain

import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoAccount(
    val profileNeedsAgreement: Boolean,
    val profileNicknameNeedsAgreement: Boolean,
    val profileImageNeedsAgreement: Boolean,
    val email: String,
    val profile: KakaoProfile,
)
