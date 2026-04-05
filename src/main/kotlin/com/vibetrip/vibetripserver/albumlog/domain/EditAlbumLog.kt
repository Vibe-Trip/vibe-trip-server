package com.vibetrip.vibetripserver.albumlog.domain

import com.vibetrip.vibetripserver.albumlog.domain.vo.Description
import org.springframework.web.multipart.MultipartFile

data class EditAlbumLog(
    val id: Long,
    val description: Description,
    val albumId: Long,
    val newImages: List<MultipartFile>,
    val removeImageIds: List<Long>,
) {
    companion object {
        fun of(
            id: Long,
            description: String,
            albumId: Long,
            newImages: List<MultipartFile>?,
            removeImageIds: List<Long>,
        ) = EditAlbumLog(
            id = id,
            description = Description(description),
            albumId = albumId,
            newImages = newImages ?: emptyList(),
            removeImageIds = removeImageIds,
        )
    }

    val descriptionValue
        get() = description.value
}
