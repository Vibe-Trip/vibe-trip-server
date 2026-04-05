package com.vibetrip.vibetripserver.album.domain

data class AlbumDetail(
    val album: Album,
    val musicUrl: String = "",
    val withLyrics: Boolean = false,
    val genre: GenreType = GenreType.LO_FI,
    val vocalGender: VocalGender = VocalGender.N,
)
