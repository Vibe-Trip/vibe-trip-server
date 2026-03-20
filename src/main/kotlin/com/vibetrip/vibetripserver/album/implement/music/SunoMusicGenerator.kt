package com.vibetrip.vibetripserver.album.implement.music

import com.vibetrip.vibetripserver.album.domain.GenerateMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import org.springframework.stereotype.Component

@Component
class SunoMusicGenerator : MusicGenerator {
    override fun generate(newAlbumMusic: NewAlbumMusic): GenerateMusic {
        // TODO: Suno API 연동
        // 프롬프트 구성 재료:
        // - newAlbumMusic.region       (여행지 → 지리적 무드)
        // - newAlbumMusic.genre        (장르 → 음악 스타일)
        // - newAlbumMusic.comment      (감정 코멘터리 → 가사/멜로디 테마)
        // - newAlbumMusic.withLyrics   (가사 포함 여부)
        // - newAlbumMusic.vocalGender  (보컬 성별)
        // + 추후 Gemini 사진 분석 키워드 추가 예정
        return GenerateMusic(
            title = "임시 트랙 제목",
            resourceUrl = "임시 노래 url",
        )
    }
}
