package com.vibetrip.vibetripserver.album.dataaccess.entity

import com.vibetrip.vibetripserver.album.domain.GeneratedMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "album_music")
class AlbumMusicEntity(
    @Column(nullable = false, length = 20)
    val title: String,
    @Column(nullable = false)
    val resourceUrl: String,
    @Column(nullable = false)
    val genre: String,
    @Column(nullable = false)
    val withLyrics: Boolean,
    @Column(nullable = true)
    val vocalGender: String?,
    @Column(nullable = false)
    val albumId: Long,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_music_id")
    var id: Long? = null,
) : BaseEntity() {
    companion object {
        fun from(
            newAlbumMusic: NewAlbumMusic,
            music: GeneratedMusic,
        ) = AlbumMusicEntity(
            title = music.title,
            resourceUrl = music.resourceUrl,
            genre = newAlbumMusic.genre,
            withLyrics = newAlbumMusic.withLyrics,
            vocalGender = newAlbumMusic.vocalGender,
            albumId = newAlbumMusic.albumId,
        )
    }
}
