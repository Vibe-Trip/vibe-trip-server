package com.vibetrip.vibetripserver.album.implement.ai

import org.springframework.stereotype.Component

@Component
class ImageAnalyzer {
    fun analyze(gcsUri: String): String {
        // TODO: Gemini API 연동 (gcsUri로 이미지 분석 후 키워드 반환)
        return ""
    }
}
