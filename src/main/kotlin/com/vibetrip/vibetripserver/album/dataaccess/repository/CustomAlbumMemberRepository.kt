package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMemberEntity

interface CustomAlbumMemberRepository {

    fun existsByAlbumIdAndMemberKey(albumId: Long, memberKey: String): Boolean
}