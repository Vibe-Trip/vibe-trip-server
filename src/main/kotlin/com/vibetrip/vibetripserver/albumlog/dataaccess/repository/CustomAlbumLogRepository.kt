package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice

interface CustomAlbumLogRepository {
    fun find(id: Long): AlbumLogEntity?

    fun findByAlbumId(
        albumId: Long,
        cursorable: Cursorable<Long>,
    ): Slice<AlbumLogEntity>

    fun delete(id: Long)
}
