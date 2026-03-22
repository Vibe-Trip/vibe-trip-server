package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.implement.AlbumCounter
import org.springframework.stereotype.Service

@Service
class AlbumService (
    private val albumCounter: AlbumCounter,
){
    fun getAlbumCount(memberKey : String) : Long {

        return albumCounter.count(memberKey)
    }
}