package com.vibetrip.vibetripserver.albumlog.domain.event

import com.vibetrip.vibetripserver.albumlog.domain.ImageData

data class AlbumLogCreatedEvent(
    val albumLogId: Long,
    val images: List<ImageData>,
)