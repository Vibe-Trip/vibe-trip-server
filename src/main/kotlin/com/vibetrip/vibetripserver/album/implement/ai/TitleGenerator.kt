package com.vibetrip.vibetripserver.album.implement.ai

import org.springframework.stereotype.Component

@Component
class TitleGenerator {
    fun generateTitle(
        region: String,
        comment: String,
        genre: String,
        imageKeywords: String,
    ): String {
        // TODO: Gemini API 연동 (region + comment + genre + imageKeywords → 제목 생성)
        return ""
    }
}
