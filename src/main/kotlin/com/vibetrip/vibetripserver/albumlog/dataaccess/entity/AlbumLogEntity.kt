package com.vibetrip.vibetripserver.albumlog.dataaccess.entity

import com.vibetrip.vibetripserver.albumlog.domain.AlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.AlbumLogImage
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Table(name = "album_log")
@Entity
class AlbumLogEntity(
    @Column(nullable = false)
    var description: String,
    @Column(nullable = false)
    var albumId: Long,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_log_id")
    var id: Long? = null,
    @Version
    var version: Long = 0,
) : BaseEntity() {
    companion object {
        fun from(newAlbumLog: NewAlbumLog) =
            AlbumLogEntity(
                description = newAlbumLog.descriptionValue,
                albumId = newAlbumLog.albumId,
            )
    }

    fun toDomain(albumLogImages: List<AlbumLogImage> = emptyList()) =
        AlbumLog.of(
            id = id!!,
            description = description,
            albumId = albumId,
            postedAt = createdAt,
            albumLogImages = albumLogImages,
        )

    fun update(description: String) {
        this.description = description
    }
}
