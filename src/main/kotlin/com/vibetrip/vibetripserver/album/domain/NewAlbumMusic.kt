package com.vibetrip.vibetripserver.album.domain

data class NewAlbumMusic(
    val albumId: Long,
    val region: String,
    val genre: String,
    val withLyrics: Boolean,
    val vocalGender: String?,
    val comment: String?,
) {
    companion object {
        fun of(
            albumId: Long,
            newAlbum: NewAlbum,
        ) = NewAlbumMusic(
            albumId = albumId,
            region = newAlbum.regionValue,
            genre = newAlbum.genre,
            withLyrics = newAlbum.withLyrics,
            vocalGender = newAlbum.vocalGender,
            comment = newAlbum.commentValue,
        )
    }
}
