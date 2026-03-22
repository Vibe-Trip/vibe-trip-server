package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageOutbox
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageOutboxRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageRepository
import com.vibetrip.vibetripserver.albumlog.domain.ImageUploadStatus
import com.vibetrip.vibetripserver.common.log.logger
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Transactional
@Component
class AlbumLogImageOutboxProcessor(
    private val outboxRepository: AlbumLogImageOutboxRepository,
    private val albumLogImageRepository: AlbumLogImageRepository,
    private val googleImageUploader: GoogleImageUploader,
) {
    fun saveOutbox(
        images: List<MultipartFile>,
        albumLogId: Long,
    ) = images.map {
        val contentType = validateImageContentType(it.contentType)
        outboxRepository
            .save(
                AlbumLogImageOutbox(
                    imageData = it.bytes,
                    contentType = contentType,
                    originalFileName = it.originalFilename ?: "",
                    albumLogId = albumLogId,
                ),
            ).id!!
    }

    fun processOutbox(outboxId: Long) {
        val item = outboxRepository.findByIdOrNull(outboxId) ?: return

        runCatching {
            googleImageUploader.uploadImage(item.toImageData())
        }.onSuccess {
            albumLogImageRepository.save(AlbumLogImageEntity(it, item.albumLogId))
            outboxRepository.delete(item)
        }.onFailure {
            logger.error { "[AlbumLogImage] 이미지 업로드 실패: outboxId=${item.id} | cause=${it.message}" }
            item.status = ImageUploadStatus.FAILED
            outboxRepository.save(item)
        }
    }
}
