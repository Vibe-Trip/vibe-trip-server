package com.vibetrip.vibetripserver.album.domain

data class AlbumMusic(
    val title: String,
    val resourceUrl: String,
    val lyrics: String = "",
)
