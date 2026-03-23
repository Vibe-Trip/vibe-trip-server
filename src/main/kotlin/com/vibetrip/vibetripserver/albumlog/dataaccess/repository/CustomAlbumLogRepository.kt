package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

interface CustomAlbumLogRepository {
    fun countByMemberKey(memberKey: String): Long
}