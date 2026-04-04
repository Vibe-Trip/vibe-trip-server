package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.ImageAnalysis
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MusicGeneratorTest : SpringTest() {
    @Autowired
    lateinit var musicGenerator: MusicGenerator

    @Test
    fun `정보를 기반으로 음악을 생성한다`() {
        // given
        val genre = GenreType.ROCK
        val vocalGender = VocalGender.F

        // when
        val response =
            musicGenerator.generate(
                genre = genre,
                vocalGender = vocalGender,
                imageAnalysis =
                    ImageAnalysis(
                        "야경이 아름다운 밤",
                        "K-Pop, Female vocal, 120 BPM, punchy drums, strong bass, bright synths, airy pads, dreamy, electric, sentimental, cinematic",
                        "[Verse 1] 비에 젖은 도쿄의 밤 네온 사인이 그려낸 그림 화려한 불빛 속에 숨겨진 나를 찾아 걸어 [Chorus] 네온 속 우리의 밤 눈부시게 빛나는 이 순간 Oh Tokyo lights guide my way 영원히 잊지 않을래 This is our story guiding me home This is our story guiding me home [Verse 2] 수많은 인파 속에도 너의 숨결이 느껴지는 듯해 낯선 거리 위에서 우연처럼 만난 너의 기억 [Bridge] 시간이 멈춘 것 같은 어둠 속의 탈출구 빛을 따라 더 높이 날아올라 [Chorus] 네온 속 우리의 밤 눈부시게 빛나는 이 순간 Oh Tokyo lights guide my way 영원히 잊지 않을래 This is our story guiding me home This is our story guiding me home [Outro] 깊어가는 밤을 날아 Neon City",
                    ),
            )

        // then
        assertThat(response.data.taskId).isNotBlank
    }
}
