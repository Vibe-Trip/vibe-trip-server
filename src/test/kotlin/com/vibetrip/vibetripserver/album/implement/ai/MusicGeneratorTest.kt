package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MusicGeneratorTest : SpringTest() {
    @Autowired
    lateinit var musicGenerator: MusicGenerator

    @Test
    fun `정보를 기반으로 음악을 생성한다`() {
        // given
        val region = "서울 남산타워"
        val comment = "야경이 아름다운 밤"

        // when
        musicGenerator.generateMusic(region, comment, GenreType.ROCK, VocalGender.F, "야경")

        // then
    }
}
