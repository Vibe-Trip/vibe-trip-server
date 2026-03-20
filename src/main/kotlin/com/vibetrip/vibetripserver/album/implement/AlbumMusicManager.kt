package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.domain.GeneratedMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
@Transactional
class AlbumMusicManager(
    private val albumMusicRepository: AlbumMusicRepository,
) {
    fun save(
        newAlbumMusic: NewAlbumMusic,
        music: GeneratedMusic,
    ) {
        albumMusicRepository.save(AlbumMusicEntity.from(newAlbumMusic, music))
    }
}
