package com.vibetrip.vibetripserver.albumlog.domain

import com.vibetrip.vibetripserver.albumlog.domain.vo.Description
import java.time.LocalDateTime

data class AlbumLog(
    val id: Long,
    val description: Description,
    val albumId: Long,
    val postedAt: LocalDateTime,
    val albumLogImages: List<AlbumLogImage>,
) {
    companion object {
        fun of(
            id: Long,
            description: String,
            albumId: Long,
            postedAt: LocalDateTime,
            albumLogImages: List<AlbumLogImage>,
        ) = AlbumLog(
            id = id,
            description = Description(description),
            albumId = albumId,
            postedAt = postedAt,
            albumLogImages = albumLogImages,
        )
    }

    val descriptionValue: String
        get() = description.value
}
