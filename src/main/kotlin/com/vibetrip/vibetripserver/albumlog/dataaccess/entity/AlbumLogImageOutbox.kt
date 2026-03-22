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
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.io.ByteArrayInputStream

@Table(name = "album_log_image_outbox")
@Entity
class AlbumLogImageOutbox(
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    var imageData: ByteArray,
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
            data = ByteArrayInputStream(imageData),
            contentType = contentType,
            originalFilename = originalFileName,
        )
}
