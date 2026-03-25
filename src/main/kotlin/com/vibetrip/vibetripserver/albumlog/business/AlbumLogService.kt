package com.vibetrip.vibetripserver.albumlog.business

import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.albumlog.domain.AlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.event.AlbumLogImageEvent
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogImageOutboxProcessor
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogManager
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

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
        images: List<MultipartFile>,
        memberKey: String,
    ): Long {
        albumMemberManager.validateMember(newAlbumLog.albumId, memberKey)

        val albumLog = albumLogManager.register(newAlbumLog)

        albumLogImageOutboxProcessor.saveOutbox(images, albumLog.id).also {
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

        return albumLogManager.findAlbumLogs(albumId, cursorable)
    }
}
