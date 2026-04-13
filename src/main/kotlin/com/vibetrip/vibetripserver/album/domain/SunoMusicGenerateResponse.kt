package com.vibetrip.vibetripserver.album.domain

data class SunoMusicGenerateResponse(
    val code: Int,
    val msg: String,
    val data: SunoMusicGenerateData,
)

data class SunoMusicGenerateData(
    val taskId: String,
)
