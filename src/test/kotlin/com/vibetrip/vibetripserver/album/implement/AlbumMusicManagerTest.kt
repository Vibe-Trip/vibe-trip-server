package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.MusicInfo
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import com.vibetrip.vibetripserver.album.domain.vo.TravelDate
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import javax.imageio.ImageIO
import kotlin.test.Test

class AlbumMusicManagerTest : SpringTest() {
    @Autowired
    lateinit var albumMusicManager: AlbumMusicManager

    @Test
    fun `정보를 기반으로 음악을 생성한다`() {
        // given
        val region = "서울 남산타워"
        val comment = "야경이 아름다운 밤"
        val image = createTestImage()
        val genre = GenreType.ROCK
        val vocalGender = VocalGender.F
        val musicInfo =
            MusicInfo(
                region = Region(region),
                comment = Comment(comment),
                travelDate = TravelDate(LocalDate.now(), LocalDate.now()),
                vocalGender = vocalGender,
                genre = genre,
            )
        val memberKey = "memberKey"

        // when
        albumMusicManager.generateMusic(
            albumId = 1L,
            musicInfo = musicInfo,
            memberKey = memberKey,
            coverImage = image,
        )

        // then
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
