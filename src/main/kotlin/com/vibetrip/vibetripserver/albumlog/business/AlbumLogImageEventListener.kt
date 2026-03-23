package com.vibetrip.vibetripserver.albumlog.business

import com.vibetrip.vibetripserver.albumlog.domain.event.AlbumLogImageEvent
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogImageOutboxProcessor
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AlbumLogImageEventListener(
    private val albumLogImageOutboxProcessor: AlbumLogImageOutboxProcessor,
) {
    @Async("imageUploadExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAlbumLogImageEvent(event: AlbumLogImageEvent) {
        event.outboxIds.forEach(albumLogImageOutboxProcessor::processOutbox)
    }
}
