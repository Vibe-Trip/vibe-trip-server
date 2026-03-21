package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.GeneratedMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic

interface MusicGenerator {
    fun generateMusic(newAlbumMusic: NewAlbumMusic): GeneratedMusic
}
