package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity

interface CustomAlbumMusicRepository {
    fun findByAlbumId(albumId: Long): AlbumMusicEntity?

    fun deleteByAlbumId(albumId: Long)
}
