package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.implement.AiProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumCoverImageProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
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
import org.junit.jupiter.api.assertThrows
import org.springframework.web.multipart.MultipartFile

class AlbumServiceTest {
    private val albumMemberRepository = mockk<AlbumMemberRepository>()
    private val albumRepository = mockk<AlbumRepository>()
    private val aiProcessor = mockk<AiProcessor>()
    private val googleImageUploader = mockk<GoogleImageUploader>()

    private lateinit var albumService: AlbumService

    @AfterEach
    fun tearDown() {
        clearMocks(albumRepository, aiProcessor, googleImageUploader, albumMemberRepository)
    }

    @BeforeEach
    fun setUp() {
        albumService =
            AlbumService(
                albumManager = AlbumManager(albumRepository, albumMemberRepository),
                aiProcessor = aiProcessor,
                albumCoverImageProcessor = AlbumCoverImageProcessor(googleImageUploader, "test-bucket"),
                albumMemberManager = AlbumMemberManager(albumMemberRepository),
            )
    }

    @Test
    fun `정상 요청이면 생성된 albumId가 반환된다`() {
        // given
        val newAlbum = AlbumFixture.newAlbum()
        val image = mockk<MultipartFile>()
        val imageKeywords = "도시, 야경, 활기찬"

        every { image.contentType } returns "image/jpeg"
        every { image.size } returns 1024L
        every { image.inputStream } returns mockk(relaxed = true)
        every { image.originalFilename } returns "test.jpg"
        every { googleImageUploader.uploadImage(any()) } returns "https://storage.googleapis.com/test.jpg"
        every { albumRepository.save(any()) } returns AlbumFixture.albumEntity(id = 1L)
        every { albumMemberRepository.save(any()) } returns mockk()
        every { aiProcessor.analyzeImage(any()) } returns imageKeywords
        justRun { aiProcessor.generateTitle(1L, newAlbum, imageKeywords) }
        justRun { aiProcessor.generateMusic(1L, newAlbum, imageKeywords) }

        // when
        val result = albumService.createAlbum(newAlbum, image)

        // then
        assertThat(result.albumId).isEqualTo(1L)
    }

    @Test
    fun `정상 요청이면 AI 분석과 제목 및 음악 생성이 호출된다`() {
        // given
        val newAlbum = AlbumFixture.newAlbum()
        val image = mockk<MultipartFile>()
        val imageKeywords = "도시, 야경, 활기찬"

        every { image.contentType } returns "image/jpeg"
        every { image.size } returns 1024L
        every { image.inputStream } returns mockk(relaxed = true)
        every { image.originalFilename } returns "test.jpg"
        every { googleImageUploader.uploadImage(any()) } returns "https://storage.googleapis.com/test.jpg"
        every { albumRepository.save(any()) } returns AlbumFixture.albumEntity(id = 1L)
        every { albumMemberRepository.save(any()) } returns mockk()
        every { aiProcessor.analyzeImage(any()) } returns imageKeywords
        justRun { aiProcessor.generateTitle(1L, newAlbum, imageKeywords) }
        justRun { aiProcessor.generateMusic(1L, newAlbum, imageKeywords) }

        // when
        albumService.createAlbum(newAlbum, image)

        // then
        verify(exactly = 1) { aiProcessor.analyzeImage(any()) }
        verify(exactly = 1) { aiProcessor.generateTitle(1L, newAlbum, imageKeywords) }
        verify(exactly = 1) { aiProcessor.generateMusic(1L, newAlbum, imageKeywords) }
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

    @Test
    fun `앨범 멤버이고 앨범이 존재하면 앨범이 수정된다`() {
        // given
        val memberKey = "member-key-123"
        val editAlbum = AlbumFixture.editAlbum(albumId = 1L)
        val entity = AlbumFixture.albumEntity(id = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumRepository.find(1L) } returns entity

        // when
        albumService.updateAlbum(editAlbum, memberKey)

        // then
        assertThat(entity.region).isEqualTo(editAlbum.region.value)
    }

    @Test
    fun `앨범 수정 시 앨범 멤버가 아니라면 NOT_ALBUM_MEMBER 예외가 발생한다`() {
        // given
        val memberKey = "member-key-123"
        val editAlbum = AlbumFixture.editAlbum(albumId = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns false

        // when & then
        val exception =
            assertThrows<AppException> {
                albumService.updateAlbum(editAlbum, memberKey)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_ALBUM_MEMBER)
    }

    @Test
    fun `앨범 수정 시 앨범이 존재하지 않으면 NOT_FOUND_ALBUM 예외가 발생한다`() {
        // given
        val memberKey = "member-key-123"
        val editAlbum = AlbumFixture.editAlbum(albumId = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumRepository.find(1L) } returns null

        // when & then
        val exception =
            assertThrows<AppException> {
                albumService.updateAlbum(editAlbum, memberKey)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND_ALBUM)
    }

    @Test
    fun `앨범 멤버이고 이미지가 없으면 기존 coverImageUrl이 유지된다`() {
        // given
        val memberKey = "member-key-123"
        val editAlbum = AlbumFixture.editAlbum(albumId = 1L)
        val entity = AlbumFixture.albumEntity(id = 1L)
        val originalCoverImageUrl = entity.coverImageUrl

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumRepository.find(1L) } returns entity

        // when
        albumService.updateAlbum(editAlbum, memberKey)

        // given
        assertThat(entity.coverImageUrl).isEqualTo(originalCoverImageUrl)
    }

    @Test
    fun `앨범 멤버이고 이미지가 있으면 새 coverImageUrl로 수정된다`() {
        // given
        val memberKey = "member-key-123"
        val image = mockk<MultipartFile>()
        val editAlbum = AlbumFixture.editAlbum(albumId = 1L, image = image)
        val entity = AlbumFixture.albumEntity(id = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumRepository.find(1L) } returns entity
        every { image.contentType } returns "image/jpeg"
        every { image.inputStream } returns mockk(relaxed = true)
        every { image.originalFilename } returns "image/jpeg"
        every { googleImageUploader.uploadImage(any()) } returns "https://storage.googleapis.com/new.jpeg"

        // when
        albumService.updateAlbum(editAlbum, memberKey)

        // then
        assertThat(entity.coverImageUrl).isEqualTo("https://storage.googleapis.com/new.jpeg")
    }
}
