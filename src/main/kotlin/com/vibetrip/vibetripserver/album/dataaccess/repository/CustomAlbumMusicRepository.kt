package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity

interface CustomAlbumMusicRepository {
    fun findByTaskId(taskId: String): AlbumMusicEntity?
}
