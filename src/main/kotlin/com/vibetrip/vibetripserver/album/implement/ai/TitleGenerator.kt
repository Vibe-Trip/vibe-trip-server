package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic

interface TitleGenerator {
    fun generateTitle(newAlbumMusic: NewAlbumMusic): String
}
