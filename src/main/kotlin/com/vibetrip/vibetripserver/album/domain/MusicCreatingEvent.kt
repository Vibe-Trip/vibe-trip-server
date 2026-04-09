package com.vibetrip.vibetripserver.album.domain

data class MusicCreatingEvent(
    val albumId: Long,
    val taskId: String,
    val memberKey: String,
)
