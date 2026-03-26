package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.implement.AiProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumFinder
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile

class AlbumServiceTest {
    private val albumRepository = mockk<AlbumRepository>()
    private val aiProcessor = mockk<AiProcessor>()

    private lateinit var albumService: AlbumService

    @AfterEach
    fun tearDown() {
        clearMocks(albumRepository, aiProcessor)
    }

    @BeforeEach
    fun setUp() {
        albumService = AlbumService(
            albumManager = AlbumManager(albumRepository),
            aiProcessor = aiProcessor,
            albumFinder = AlbumFinder(albumRepository),
        )
    }

    @Test
    fun `정상 요청이면 생성된 albumId가 반환된다`() {
        // given
        val newAlbum = AlbumFixture.newAlbum()
        val image = mockk<MultipartFile>()
        val imageKeywords = "도시, 야경, 활기찬"

        every { albumRepository.save(any()) } returns AlbumFixture.albumEntity(id = 1L)
        every { aiProcessor.analyzeImage(any()) } returns imageKeywords
        justRun { aiProcessor.generateTitle(1L, newAlbum, imageKeywords) }
        justRun { aiProcessor.generateMusic(1L, newAlbum, imageKeywords) }

        // when
        val result = albumService.create(newAlbum, image)

        // then
        assertThat(result.albumId).isEqualTo(1L)
    }

    @Test
    fun `정상 요청이면 AI 분석과 제목 및 음악 생성이 호출된다`() {
        // given
        val newAlbum = AlbumFixture.newAlbum()
        val image = mockk<MultipartFile>()
        val imageKeywords = "도시, 야경, 활기찬"

        every { albumRepository.save(any()) } returns AlbumFixture.albumEntity(id = 1L)
        every { aiProcessor.analyzeImage(any()) } returns imageKeywords
        justRun { aiProcessor.generateTitle(1L, newAlbum, imageKeywords) }
        justRun { aiProcessor.generateMusic(1L, newAlbum, imageKeywords) }

        // when
        albumService.create(newAlbum, image)

        // then
        verify(exactly = 1) { aiProcessor.analyzeImage(any()) }
        verify(exactly = 1) { aiProcessor.generateTitle(1L, newAlbum, imageKeywords) }
        verify(exactly = 1) { aiProcessor.generateMusic(1L, newAlbum, imageKeywords) }
    }

    @Test
    fun `앨범이 존재하면 최신순으로 앨범 목록이 반환된다`() {
        // given
        val memberKey = "member-key-123"
        val cursorable = Cursorable<Long>(cursor = null, limit = 10)
        val albumEntities = listOf(
            AlbumFixture.albumEntity(id = 2L),
            AlbumFixture.albumEntity(id = 1L),
        )

        every { albumRepository.findAllByMemberKey(memberKey, cursorable) } returns
            Slice(albumEntities, cursorable, hasNext = false)

        // when
        val result = albumService.getAlbums(memberKey, cursorable)

        // then
        assertThat(result.content.size).isEqualTo(2)
        assertThat(result.content[0].albumId).isEqualTo(2L)
        assertThat(result.content[1].albumId).isEqualTo(1L)
    }

    @Test
    fun `다음 페이지가 있으면 hasNext가 true다`() {
        // given
        val memberKey = "member-key-123"
        val cursorable = Cursorable<Long>(cursor = null, limit = 2)
        val albumEntities = listOf(
            AlbumFixture.albumEntity(id = 3L),
            AlbumFixture.albumEntity(id = 2L),
        )

        every { albumRepository.findAllByMemberKey(memberKey, cursorable) } returns
            Slice(albumEntities, cursorable, hasNext = true)

        // when
        val result = albumService.getAlbums(memberKey, cursorable)

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
        val result = albumService.getAlbums(memberKey, cursorable)

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