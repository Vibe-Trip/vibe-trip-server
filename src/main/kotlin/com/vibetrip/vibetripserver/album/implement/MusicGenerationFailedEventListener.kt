package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.domain.MusicGenerationFailedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MusicGenerationFailedEventListener(
    private val albumManager: AlbumManager,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handle(event: MusicGenerationFailedEvent) {
        albumManager.delete(event.albumId)
    }
}
