package com.vibetrip.vibetripserver.album.domain

import java.time.LocalDateTime

data class SunoMusicData(
    val taskId: String,
    val id: String,
    val audioUrl: String,
    val sourceAudioUrl: String,
    val streamAudioUrl: String,
    val sourceStreamAudioUrl: String,
    val imageUrl: String,
    val sourceImageUrl: String,
    val prompt: String,
    val modelName: String,
    val title: String,
    val tags: String,
    val createTime: LocalDateTime,
    val duration: Double,
    var sunoMusicDataId: Long? = null,
)
