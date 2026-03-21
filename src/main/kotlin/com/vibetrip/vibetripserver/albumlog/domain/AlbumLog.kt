package com.vibetrip.vibetripserver.albumlog.domain

import com.vibetrip.vibetripserver.albumlog.domain.vo.Description

data class AlbumLog(
    val id: Long,
    val description: Description,
    val albumId: Long,
) {
    companion object {
        fun of(
            id: Long,
            description: String,
            albumId: Long,
        ) = AlbumLog(
            id = id,
            description = Description(description),
            albumId = albumId,
        )
    }

    val descriptionValue: String
        get() = description.value
}