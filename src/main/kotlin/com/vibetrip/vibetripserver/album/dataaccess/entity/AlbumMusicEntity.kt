package com.vibetrip.vibetripserver.album.dataaccess.entity

import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.*

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

    @Column(nullable = false)
    val vocalGender: String,

    @Column(nullable = false)
    val albumId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_music_id")
    var id: Long? = null,
) : BaseEntity()