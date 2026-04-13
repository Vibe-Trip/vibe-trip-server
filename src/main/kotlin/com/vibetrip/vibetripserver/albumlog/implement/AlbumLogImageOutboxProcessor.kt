package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageOutbox
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageOutboxRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageRepository
import com.vibetrip.vibetripserver.albumlog.domain.ImageUploadStatus
import com.vibetrip.vibetripserver.common.log.logger
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.TempFileStorage
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import kotlin.io.path.exists

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
    ): List<Long> {
        val savedPaths = mutableListOf<Path>()

        return runCatching {
            images.map { saveImage(it, albumLogId, savedPaths) }
        }.onFailure {
            savedPaths.forEach(TempFileStorage::delete)
        }.getOrThrow()
    }

    private fun saveImage(
        image: MultipartFile,
        albumLogId: Long,
        savedPaths: MutableList<Path>,
    ): Long {
        val contentType = validateImageContentType(image.contentType)
        val path = TempFileStorage.save(image).also { savedPaths.add(it) }

        return outboxRepository
            .save(
                AlbumLogImageOutbox(
                    tempFilePath = path.toString(),
                    contentType = contentType,
                    originalFileName = path.fileName.toString(),
                    albumLogId = albumLogId,
                ),
            ).id!!
    }

    fun processOutbox(outboxId: Long) {
        val item = outboxRepository.findByIdOrNull(outboxId) ?: return
        val tempFilePath =
            Path.of(item.tempFilePath).takeIf { it.exists() }
                ?: return handleFailure(item, "임시 파일 없음")

        runCatching {
            item.toImageData().use { googleImageUploader.uploadImage(it) }
        }.onSuccess { url ->
            albumLogImageRepository.save(AlbumLogImageEntity(url, item.albumLogId))
            outboxRepository.delete(item)
            TempFileStorage.delete(tempFilePath)
        }.onFailure { handleFailure(item, "업로드 실패: ${it.message}") }
    }

    private fun handleFailure(
        item: AlbumLogImageOutbox,
        reason: String,
    ) {
        logger.error { "[AlbumLogImage] $reason: outboxId=${item.id}" }
        item.status = ImageUploadStatus.FAILED
        outboxRepository.save(item)
    }
}
