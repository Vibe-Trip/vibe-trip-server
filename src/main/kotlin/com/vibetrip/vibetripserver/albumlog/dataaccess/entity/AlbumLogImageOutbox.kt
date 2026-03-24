package com.vibetrip.vibetripserver.albumlog.dataaccess.entity

import com.vibetrip.vibetripserver.albumlog.domain.ImageUploadStatus
import com.vibetrip.vibetripserver.common.domain.ImageData
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.nio.file.Files
import java.nio.file.Path

@Table(name = "album_log_image_outbox")
@Entity
class AlbumLogImageOutbox(
    @Column(nullable = false)
    var tempFilePath: String,
    @Column(nullable = false)
    var contentType: String,
    @Column(nullable = false)
    var originalFileName: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ImageUploadStatus = ImageUploadStatus.PENDING,
    @Column(nullable = false)
    var albumLogId: Long,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_log_image_id")
    var id: Long? = null,
) {
    fun toImageData() =
        ImageData(
            data = Files.newInputStream(Path.of(tempFilePath)),
            contentType = contentType,
            originalFilename = originalFileName,
        )
}
