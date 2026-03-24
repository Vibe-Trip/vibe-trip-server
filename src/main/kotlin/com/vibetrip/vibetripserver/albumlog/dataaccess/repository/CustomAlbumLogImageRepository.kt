package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity

interface CustomAlbumLogImageRepository {
    fun findByAlbumLogIds(albumLogIds: List<Long>): List<AlbumLogImageEntity>
}
