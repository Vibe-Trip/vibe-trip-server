package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AlbumMusicRepository :
    JpaRepository<AlbumMusicEntity, Long>,
    CustomAlbumMusicRepository
