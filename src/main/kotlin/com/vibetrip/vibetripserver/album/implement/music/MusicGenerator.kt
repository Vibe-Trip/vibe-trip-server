package com.vibetrip.vibetripserver.album.implement.music

import com.vibetrip.vibetripserver.album.domain.GeneratedMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic

interface MusicGenerator {
    fun generateMusic(newAlbumMusic: NewAlbumMusic): GeneratedMusic
}
