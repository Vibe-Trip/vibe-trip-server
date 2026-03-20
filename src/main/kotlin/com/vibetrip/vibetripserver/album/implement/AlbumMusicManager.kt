package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.domain.GenerateMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
@Transactional
class AlbumMusicManager(
    private val albumMusicRepository: AlbumMusicRepository,
) {
    fun save(newAlbumMusic: NewAlbumMusic, music: GenerateMusic) {
        albumMusicRepository.save(AlbumMusicEntity.from(newAlbumMusic, music))
    }
}