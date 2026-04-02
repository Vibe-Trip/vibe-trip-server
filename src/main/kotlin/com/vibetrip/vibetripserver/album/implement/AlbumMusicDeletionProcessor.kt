package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import org.springframework.stereotype.Component

@Component
class AlbumMusicDeletionProcessor(
    private val albumMusicRepository: AlbumMusicRepository,
) : AlbumDeletionProcessor {
    override fun process(albumId: Long) {
        albumMusicRepository.deleteByAlbumId(albumId)
    }
}
