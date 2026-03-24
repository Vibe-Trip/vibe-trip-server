package com.vibetrip.vibetripserver.albumlog.presentation.dto.response

import com.vibetrip.vibetripserver.albumlog.domain.AlbumLog
import java.time.LocalDateTime

data class AlbumLogListResponse(
    val id: Long,
    val description: String,
    val postedAt: LocalDateTime,
    val images: List<AlbumLogImageResponse>,
) {
    companion object {
        fun from(albumLog: AlbumLog) =
            AlbumLogListResponse(
                id = albumLog.id,
                description = albumLog.descriptionValue,
                postedAt = albumLog.postedAt,
                images = albumLog.albumLogImages.map { AlbumLogImageResponse(it.imageUrl) },
            )
    }
}
