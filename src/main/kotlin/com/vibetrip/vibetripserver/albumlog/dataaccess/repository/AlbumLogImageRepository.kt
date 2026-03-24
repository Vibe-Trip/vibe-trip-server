package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AlbumLogImageRepository :
    JpaRepository<AlbumLogImageEntity, Long>,
    CustomAlbumLogImageRepository
