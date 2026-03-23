package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
@Transactional
class AlbumMusicManager(
    private val albumMusicRepository: AlbumMusicRepository,
) {
    fun save(
        albumId: Long,
        newAlbum: NewAlbum,
        music: AlbumMusic,
    ) {
        albumMusicRepository.save(AlbumMusicEntity.from(albumId, newAlbum, music))
    }
}
