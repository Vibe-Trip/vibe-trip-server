package com.vibetrip.vibetripserver.album.domain

data class MusicCompletedEvent(
    val albumId: Long,
    val taskId: String,
    val memberKey: String,
    val title: String,
)
