package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice

interface CustomAlbumLogRepository {
    fun find(id: Long): AlbumLogEntity?

    fun countByMemberKey(memberKey: String): Long

    fun countByAlbumId(albumId: Long): Long

    fun findByAlbumId(
        albumId: Long,
        cursorable: Cursorable<Long>,
    ): Slice<AlbumLogEntity>

    fun delete(id: Long)

    fun deleteByMemberKey(memberKey: String)

    fun deleteByAlbumId(albumId: Long)

    fun findIdsByAlbumId(albumId: Long): List<Long>
}
