package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.album.implement.AlbumDeletionProcessor
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import org.springframework.stereotype.Component

@Component
class AlbumLogAndImageDeletionProcessor(
    private val albumLogRepository: AlbumLogRepository,
    private val albumLogImageRepository: AlbumLogImageRepository,
) : AlbumDeletionProcessor {
    override fun process(albumId: Long) {
        val logIds = albumLogRepository.findIdsByAlbumId(albumId)
        if (logIds.isEmpty()) return
        val logImageIds = albumLogImageRepository.findByAlbumLogIds(logIds).map { it.id!! }
        albumLogImageRepository.deleteByIds(logImageIds)
        albumLogRepository.deleteByAlbumId(albumId)
    }
}
