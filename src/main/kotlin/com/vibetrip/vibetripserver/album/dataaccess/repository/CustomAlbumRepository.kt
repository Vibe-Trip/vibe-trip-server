package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice

interface CustomAlbumRepository {
    fun find(albumId: Long): AlbumEntity?

    fun findAllByMemberKey(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ): Slice<AlbumEntity>

    fun countByMemberKey(memberKey: String): Long

    fun deleteByAlbumId(albumId: Long)

    fun deleteByMemberKey(memberKey: String)
}
