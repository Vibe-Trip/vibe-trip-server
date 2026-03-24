package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity

interface CustomAlbumLogRepository {
    fun find(id: Long): AlbumLogEntity?

    fun countByMemberKey(memberKey: String): Long
}
