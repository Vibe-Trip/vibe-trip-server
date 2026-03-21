package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.albumlog.domain.ImageUploadStatus
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class AlbumLogManager(
    private val albumLogRepository: AlbumLogRepository,
) {

    fun register(newAlbumLog: NewAlbumLog) =
        albumLogRepository.save(AlbumLogEntity.from(newAlbumLog)).toDomain()

    fun updateImageUploadStatus(id: Long, status: ImageUploadStatus) {
        albumLogRepository.find(id)?.let {
            it.imageUploadStatus = status
        } ?: throw AppException(ErrorType.NOT_FOUND_DATA)
    }
}