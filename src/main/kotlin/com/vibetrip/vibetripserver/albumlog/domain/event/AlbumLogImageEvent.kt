package com.vibetrip.vibetripserver.albumlog.domain.event

data class AlbumLogImageEvent(
    val albumLogId: Long,
    val outboxIds: List<Long>,
)
