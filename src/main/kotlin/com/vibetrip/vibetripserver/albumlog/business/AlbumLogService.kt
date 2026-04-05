package com.vibetrip.vibetripserver.albumlog.business

import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.albumlog.domain.AlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.EditAlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.event.AlbumLogImageEvent
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogImageOutboxProcessor
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogManager
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import org.springframework.context.ApplicationEventPublisher
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AlbumLogService(
    private val albumMemberManager: AlbumMemberManager,
    private val albumLogManager: AlbumLogManager,
    private val eventPublisher: ApplicationEventPublisher,
    private val albumLogImageOutboxProcessor: AlbumLogImageOutboxProcessor,
) {
    @Transactional
    fun registerAlbumLog(
        newAlbumLog: NewAlbumLog,
        memberKey: String,
    ): Long {
        albumMemberManager.validateMember(newAlbumLog.albumId, memberKey)

        val albumLog = albumLogManager.register(newAlbumLog)

        albumLogImageOutboxProcessor.saveOutbox(newAlbumLog.images, albumLog.id).also {
            eventPublisher.publishEvent(AlbumLogImageEvent(albumLog.id, it))
        }

        return albumLog.id
    }

    fun getAlbumLogCount(memberKey: String): Long = albumLogManager.count(memberKey)

    fun findAlbumLogs(
        albumId: Long,
        cursorable: Cursorable<Long>,
        memberKey: String,
    ): Slice<AlbumLog> {
        albumMemberManager.validateMember(albumId, memberKey)

        return albumLogManager.find(albumId, cursorable)
    }

    @Retryable(
        retryFor = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 100),
    )
    @Transactional
    fun updateAlbumLog(
        editAlbumLog: EditAlbumLog,
        memberKey: String,
    ) {
        albumMemberManager.validateMember(editAlbumLog.albumId, memberKey)

        albumLogManager.update(editAlbumLog.id, editAlbumLog.descriptionValue, editAlbumLog.removeImageIds)

        albumLogImageOutboxProcessor.saveOutbox(editAlbumLog.newImages, editAlbumLog.id).also {
            eventPublisher.publishEvent(AlbumLogImageEvent(editAlbumLog.id, it))
        }
    }

    fun deleteAlbumLog(
        albumId: Long,
        albumLogId: Long,
        memberKey: String,
    ) {
        albumMemberManager.validateMember(albumId, memberKey)

        albumLogManager.delete(albumLogId)
    }

    fun findAlbumLogCount(albumId: Long) = albumLogManager.count(albumId)

    fun findAlbumLogImages(
        albumId: Long,
        count: Long,
    ) = albumLogManager.albumLogImages(albumId, count)
}
