package com.vibetrip.vibetripserver.albumlog.presentation.dto.request

import jakarta.validation.constraints.NotBlank

data class AlbumLogUpdateRequest(
    @field:NotBlank(message = "설명은 필수입니다.")
    val description: String,
    val removeImageIds: List<Long>,
)
