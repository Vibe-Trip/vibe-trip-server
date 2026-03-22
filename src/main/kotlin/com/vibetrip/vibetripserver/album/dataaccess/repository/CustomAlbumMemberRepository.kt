package com.vibetrip.vibetripserver.album.dataaccess.repository

interface CustomAlbumMemberRepository {
    fun existsByAlbumIdAndMemberKey(
        albumId: Long,
        memberKey: String,
    ): Boolean
}
