package com.vibetrip.vibetripserver.albumlog.dataaccess.entity

import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "album_log_image")
@Entity
class AlbumLogImageEntity(
    @Column(nullable = false)
    var imageUrl: String,
    @Column(nullable = false)
    var albumLogId: Long,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_log_image_id")
    var id: Long? = null,
) : BaseEntity()
