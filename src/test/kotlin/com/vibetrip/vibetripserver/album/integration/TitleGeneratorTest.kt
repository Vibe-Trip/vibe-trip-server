package com.vibetrip.vibetripserver.album.integration

import com.vibetrip.vibetripserver.album.implement.ai.TitleGenerator
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class TitleGeneratorTest : SpringTest() {
    @Autowired
    lateinit var titleGenerator: TitleGenerator

    @Test
    fun `이미지와 정보를 기반으로 제목을 생성한다`() {
        // given
        val region = "서울 남산타워"
        val comment = "야경이 아름다운 밤"
        val image = createTestImage()

        // when
        val title = titleGenerator.generateTitle(region, comment, image).title

        // then
        println("생성된 제목: $title")
        assertThat(title).isNotBlank()
        assertThat(title.length).isLessThanOrEqualTo(15)
    }

    @Test
    fun `제주도 여행 사진에 대한 제목을 생성한다`() {
        // given
        val region = "제주도 성산일출봉"
        val comment = "일출을 보며 새해 소원을 빌었다"
        val image = createTestImage()

        // when
        val title = titleGenerator.generateTitle(region, comment, image).title

        // then
        println("생성된 제목: $title")
        assertThat(title).isNotBlank()
        assertThat(title.length).isLessThanOrEqualTo(15)
    }

    private fun createTestImage(): MockMultipartFile {
        val image = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        graphics.fillRect(0, 0, 100, 100)
        graphics.dispose()

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", outputStream)

        return MockMultipartFile(
            "image",
            "test-image.jpg",
            "image/jpeg",
            outputStream.toByteArray(),
        )
    }
}
