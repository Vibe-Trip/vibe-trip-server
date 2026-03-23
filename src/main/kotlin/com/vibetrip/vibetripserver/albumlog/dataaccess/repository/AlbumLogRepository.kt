package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AlbumLogRepository : JpaRepository<AlbumLogEntity, Long>,
    CustomAlbumLogRepository
