package com.vibetrip.vibetripserver.albumlog.domain

import com.vibetrip.vibetripserver.albumlog.domain.vo.Description

data class NewAlbumLog(
    val description: Description,
    val albumId: Long,
) {
    companion object {
        fun of(
            description: String,
            albumId: Long,
        ) = NewAlbumLog(
            description = Description(description),
            albumId = albumId,
        )
    }

    val descriptionValue: String
        get() = description.value
}