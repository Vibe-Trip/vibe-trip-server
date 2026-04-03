package com.vibetrip.vibetripserver.album.domain

data class AlbumMusic(
    val title: String,
    val musicUrl: String,
    val lyrics: String = "",
)
