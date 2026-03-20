package com.vibetrip.vibetripserver.album.implement.music

import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic

interface TitleGenerator {
    fun generateTitle(newAlbumMusic: NewAlbumMusic): String
}
