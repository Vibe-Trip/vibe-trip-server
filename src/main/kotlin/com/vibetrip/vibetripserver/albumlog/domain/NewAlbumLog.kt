package com.vibetrip.vibetripserver.albumlog.domain

import com.vibetrip.vibetripserver.albumlog.domain.vo.Description
import org.springframework.web.multipart.MultipartFile

data class NewAlbumLog(
    val description: Description,
    val images: List<MultipartFile>,
    val albumId: Long,
) {
    companion object {
        fun of(
            description: String,
            images: List<MultipartFile>?,
            albumId: Long,
        ) = NewAlbumLog(
            description = Description(description),
            images = images ?: emptyList(),
            albumId = albumId,
        )
    }

    val descriptionValue: String
        get() = description.value
}
