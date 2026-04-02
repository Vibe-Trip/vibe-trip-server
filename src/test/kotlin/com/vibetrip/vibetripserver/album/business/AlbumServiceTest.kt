package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.album.implement.ai.ImageAnalyzer
import com.vibetrip.vibetripserver.album.implement.ai.MusicGenerator
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.multipart.MultipartFile

class AlbumServiceTest {
    private val albumRepository = mockk<AlbumRepository>()
    private val albumMusicRepository = mockk<AlbumMusicRepository>()
    private val albumMemberRepository = mockk<AlbumMemberRepository>()
    private val googleImageUploader = mockk<GoogleImageUploader>()
    private val imageAnalyzer = mockk<ImageAnalyzer>()
    private val musicGenerator = mockk<MusicGenerator>()

    private lateinit var albumService: AlbumService

    @AfterEach
    fun tearDown() {
        clearMocks(
            albumRepository,
            albumMusicRepository,
            googleImageUploader,
            albumMemberRepository,
            imageAnalyzer,
            musicGenerator,
        )
    }

    @BeforeEach
    fun setUp() {
        albumService =
            AlbumService(
                albumManager = AlbumManager(albumRepository, albumMemberRepository),
                albumMusicManager = AlbumMusicManager(albumMusicRepository),
                googleImageUploader = googleImageUploader,
                imageAnalyzer = imageAnalyzer,
                musicGenerator = musicGenerator,
            )
    }

    @Test
    fun `유효하지 않은 이미지 타입이면 INVALID_IMAGE_TYPE 예외가 발생한다`() {
        // given
        val newAlbum = AlbumFixture.newAlbum()
        val image = mockk<MultipartFile>()

        every { image.contentType } returns "application/pdf"

        // when & then
        val exception =
            assertThrows<AppException> {
                albumService.createAlbum(newAlbum, image)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.INVALID_IMAGE_TYPE)
    }

    @Test
    fun `앨범이 존재하면 앨범 목록이 반환된다`() {
        // given
        val memberKey = "member-key-123"
        val cursorable = Cursorable<Long>(cursor = null, limit = 10)
        val albumEntities =
            listOf(
                AlbumFixture.albumEntity(id = 2L),
                AlbumFixture.albumEntity(id = 1L),
            )

        every { albumRepository.findAllByMemberKey(memberKey, cursorable) } returns
            Slice(albumEntities, cursorable, hasNext = false)

        // when
        val result = albumService.findAlbums(memberKey, cursorable)

        // then
        assertThat(result.content.size).isEqualTo(2)
    }

    @Test
    fun `다음 페이지가 있으면 hasNext가 true다`() {
        // given
        val memberKey = "member-key-123"
        val cursorable = Cursorable<Long>(cursor = null, limit = 2)
        val albumEntities =
            listOf(
                AlbumFixture.albumEntity(id = 3L),
                AlbumFixture.albumEntity(id = 2L),
            )

        every { albumRepository.findAllByMemberKey(memberKey, cursorable) } returns
            Slice(albumEntities, cursorable, hasNext = true)

        // when
        val result = albumService.findAlbums(memberKey, cursorable)

        // then
        assertThat(result.hasNext).isEqualTo(true)
    }

    @Test
    fun `앨범이 없으면 빈 목록이 반환된다`() {
        // given
        val memberKey = "member-key-123"
        val cursorable = Cursorable<Long>(cursor = null, limit = 10)

        every { albumRepository.findAllByMemberKey(memberKey, cursorable) } returns
            Slice(emptyList(), cursorable, hasNext = false)

        // when
        val result = albumService.findAlbums(memberKey, cursorable)

        // then
        assertThat(result.content).isEmpty()
        assertThat(result.hasNext).isEqualTo(false)
    }

    @Test
    fun `앨범의 총 개수가 반환된다`() {
        // given
        val memberKey = "member-key-123"

        every { albumRepository.countByMemberKey(memberKey) } returns 5L

        // when
        val result = albumService.countAlbums(memberKey)

        // then
        assertThat(result).isEqualTo(5L)
    }
}
