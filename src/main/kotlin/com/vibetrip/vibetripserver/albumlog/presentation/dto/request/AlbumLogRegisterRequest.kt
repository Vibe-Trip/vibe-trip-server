package com.vibetrip.vibetripserver.albumlog.presentation.dto.request

import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import jakarta.validation.constraints.NotBlank

data class AlbumLogRegisterRequest(
    @field:NotBlank(message = "설명은 필수입니다.")
    val description: String,
) {
    fun toNewAlbumLog(albumId: Long) = NewAlbumLog.of(
        description = description,
        albumId = albumId,
    )
}