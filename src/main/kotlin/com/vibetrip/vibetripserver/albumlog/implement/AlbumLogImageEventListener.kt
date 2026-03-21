package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.domain.ImageUploadStatus
import com.vibetrip.vibetripserver.albumlog.domain.event.AlbumLogCreatedEvent
import com.vibetrip.vibetripserver.common.log.logger
import com.vibetrip.vibetripserver.common.util.TempFileStorage
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AlbumLogImageEventListener(
    private val albumLogImageUploader: AlbumLogImageUploader,
    private val albumLogManager: AlbumLogManager,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAlbumLogCreated(event: AlbumLogCreatedEvent) {
        try {
            albumLogImageUploader.uploadImages(event.images, event.albumLogId)
            albumLogManager.updateImageUploadStatus(event.albumLogId, ImageUploadStatus.COMPLETED)
        } catch (e: Exception) {
            logger.error { "[AlbumLogImage] 이미지 업로드 실패: albumLogId=${event.albumLogId} | cause=${e.message}" }
            albumLogManager.updateImageUploadStatus(event.albumLogId, ImageUploadStatus.FAILED)
        } finally {
            event.images.forEach {
                TempFileStorage.delete(it.tempFilePath)
            }
        }
    }
}