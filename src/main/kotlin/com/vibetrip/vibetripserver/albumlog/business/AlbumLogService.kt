package com.vibetrip.vibetripserver.albumlog.business

import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.albumlog.domain.AlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.ImageData
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.event.AlbumLogCreatedEvent
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogManager
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.util.TempFileStorage
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AlbumLogService(
    private val albumMemberManager: AlbumMemberManager,
    private val albumLogManager: AlbumLogManager,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun registerAlbumLog(newAlbumLog: NewAlbumLog, images: List<MultipartFile>, memberKey: String): AlbumLog {
        albumMemberManager.validateMember(newAlbumLog.albumId, memberKey)

        val tempImages = images.map {
            ImageData(
                tempFilePath = TempFileStorage.save(it),
                contentType = it.contentType ?: "",
                originalFilename = it.originalFilename ?: throw AppException(ErrorType.FILE_NAME_IS_NULL)
            )
        }

        return albumLogManager.register(newAlbumLog).also {
            eventPublisher.publishEvent(AlbumLogCreatedEvent(it.id, tempImages))
        }
    }
}