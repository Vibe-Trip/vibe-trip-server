package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity

interface CustomAlbumLogImageRepository {
    fun findByAlbumLogIds(albumLogIds: List<Long>): List<AlbumLogImageEntity>

    fun findByAlbumId(
        albumId: Long,
        count: Long,
    ): List<AlbumLogImageEntity>

    fun countByAlbumId(albumId: Long): Long

    fun deleteByIds(ids: List<Long>)

    fun deleteByAlbumLogId(albumLogId: Long)
}
