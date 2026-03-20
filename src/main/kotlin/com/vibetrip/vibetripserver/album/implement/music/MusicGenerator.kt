package com.vibetrip.vibetripserver.album.implement.music

import com.vibetrip.vibetripserver.album.domain.GenerateMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic


interface MusicGenerator {
    fun generate(newAlbumMusic: NewAlbumMusic): GenerateMusic
}