package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class ImageAnalyzerTest : SpringTest() {
    @Autowired
    lateinit var imageAnalyzer: ImageAnalyzer

    @Test
    fun `이미지를 분석하여 제목과 분석 내용을 생성한다`() {
        // given
        val region = "서울 남산타워"
        val comment = "야경이 아름다운 밤"
        val image = createTestImage()

        // when
        val imageAnalysis =
            imageAnalyzer.analyze(
                region = region,
                genre = GenreType.ROCK,
                vocalGender = VocalGender.F,
                comment = comment,
                imageBytes = image.bytes,
            )

        // then
        println("이미지 분석 결과: $imageAnalysis")
        assertThat(imageAnalysis.title).isNotBlank
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
