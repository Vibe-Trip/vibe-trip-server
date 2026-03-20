package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.album.implement.music.MusicGenerator
import com.vibetrip.vibetripserver.album.implement.music.TitleGenerator
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AlbumMusicService(
    private val albumManager: AlbumManager,
    private val albumMusicManager: AlbumMusicManager,
    private val musicGenerator: MusicGenerator,
    private val titleGenerator: TitleGenerator
) {
    @Async
    fun generate(newAlbumMusic: NewAlbumMusic) {
        val title = titleGenerator.generate(newAlbumMusic)
        albumManager.updateTitle(newAlbumMusic.albumId, title)

        val music = musicGenerator.generate(newAlbumMusic)
        albumMusicManager.save(newAlbumMusic, music)
    }
}