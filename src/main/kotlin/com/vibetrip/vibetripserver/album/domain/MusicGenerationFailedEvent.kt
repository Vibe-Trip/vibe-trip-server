package com.vibetrip.vibetripserver.album.domain

data class MusicGenerationFailedEvent(
    val albumId: Long,
    val memberKey: String,
)
