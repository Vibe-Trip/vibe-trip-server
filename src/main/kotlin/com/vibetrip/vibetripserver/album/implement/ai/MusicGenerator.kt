package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.VocalGender
import org.springframework.stereotype.Component

@Component
class MusicGenerator {
    fun generateMusic(
        region: String,
        comment: String,
        genre: String,
        vocalGender: VocalGender,
        withLyrics: Boolean,
        imageKeywords: String,
    ): AlbumMusic {
        // TODO: Suno API 연동 (region + comment + genre + vocalGender + withLyrics + imageKeywords → 음악 생성)
        return AlbumMusic(title = "", resourceUrl = "")
    }
}