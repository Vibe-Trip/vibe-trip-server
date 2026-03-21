package com.vibetrip.vibetripserver.albumlog.dataaccess.entity

import com.vibetrip.vibetripserver.albumlog.domain.AlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.ImageUploadStatus
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.*

@Table(name = "album_log")
@Entity
class AlbumLogEntity(
    @Column(nullable = false)
    var description: String,
    @Column(nullable = false)
    var albumId: Long,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var imageUploadStatus: ImageUploadStatus = ImageUploadStatus.PENDING,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_log_id")
    var id: Long? = null,
) : BaseEntity() {
    companion object {
        fun from(newAlbumLog: NewAlbumLog) = AlbumLogEntity(
            description = newAlbumLog.descriptionValue,
            albumId = newAlbumLog.albumId,
        )
    }

    fun toDomain() = AlbumLog.of(
        id = id!!,
        description = description,
        albumId = albumId,
    )
}