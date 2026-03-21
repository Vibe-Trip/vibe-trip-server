package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import org.springframework.stereotype.Component

@Component
class GeminiTitleGenerator : TitleGenerator {
    override fun generateTitle(newAlbumMusic: NewAlbumMusic): String {
        // TODO: Gemini API 연동
        // 프롬프트 구성 재료:
        // - newAlbumMusic.region   (여행지)
        // - newAlbumMusic.comment  (감정 키워드)
        // - newAlbumMusic.genre    (장르 무드)
        // + 추후 Gemini 사진 분석 결과 추가 예정
        // 결과: 최대 15자 앨범 제목
        return "임시 앨범 제목"
    }
}
