package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import org.springframework.stereotype.Component

@Component
class AlbumMemberDeletionProcessor(
    private val albumMemberRepository: AlbumMemberRepository,
) : AlbumDeletionProcessor {
    override fun process(albumId: Long) {
        albumMemberRepository.deleteByAlbumId(albumId)
    }
}
