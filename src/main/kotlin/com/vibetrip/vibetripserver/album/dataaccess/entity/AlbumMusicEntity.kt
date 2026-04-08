package com.vibetrip.vibetripserver.album.dataaccess.entity

import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "album_music")
class AlbumMusicEntity(
    @Column(nullable = false, length = 20)
    val title: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var musicUrl: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val genre: GenreType,
    @Column(nullable = false)
    val withLyrics: Boolean,
    @Column(nullable = false, columnDefinition = "TEXT")
    var lyrics: String = "",
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val vocalGender: VocalGender = VocalGender.N,
    @Column(nullable = false)
    val albumId: Long,
    @Column(nullable = false)
    val taskId: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_music_id")
    var id: Long? = null,
) : BaseEntity() {
    companion object {
        fun from(
            albumId: Long,
            newAlbum: NewAlbum,
            taskId: String,
            music: AlbumMusic,
        ) = AlbumMusicEntity(
            title = music.title,
            musicUrl = music.musicUrl,
            lyrics = music.lyrics,
            genre = newAlbum.genre,
            withLyrics = newAlbum.genre.withLyrics,
            vocalGender = newAlbum.vocalGender,
            taskId = taskId,
            albumId = albumId,
        )
    }

    fun update(
        musicUrl: String,
        lyrics: String,
    ) {
        this.musicUrl = musicUrl
        this.lyrics = lyrics
    }
}
