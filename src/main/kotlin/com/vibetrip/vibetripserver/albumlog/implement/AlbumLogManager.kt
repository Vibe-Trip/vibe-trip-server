package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class AlbumLogManager(
    private val albumLogRepository: AlbumLogRepository,
) {
    fun register(newAlbumLog: NewAlbumLog) = albumLogRepository.save(AlbumLogEntity.from(newAlbumLog)).toDomain()
}
