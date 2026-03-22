package com.vibetrip.vibetripserver.albumlog.business

import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogCounter
import org.springframework.stereotype.Service

@Service
class AlbumLogService(
    private val albumLogCounter: AlbumLogCounter,
) {
    fun getAlbumLogCount(memberKey: String): Long = albumLogCounter.count(memberKey)
}
