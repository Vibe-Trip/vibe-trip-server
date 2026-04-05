package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.alarm.implement.AlarmManager
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.SunoMusicDataRepository
import com.vibetrip.vibetripserver.album.domain.ImageAnalysis
import com.vibetrip.vibetripserver.album.domain.SunoMusicGenerateData
import com.vibetrip.vibetripserver.album.domain.SunoMusicGenerateResponse
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
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
import io.mockk.justRun
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.multipart.MultipartFile

class AlbumServiceTest {
    private val albumMusicRepository = mockk<AlbumMusicRepository>()
    private val albumRepository = mockk<AlbumRepository>()
    private val albumMemberRepository = mockk<AlbumMemberRepository>()
    private val googleImageUploader = mockk<GoogleImageUploader>()
    private val sunoMusicDataRepository = mockk<SunoMusicDataRepository>()
    private val musicGenerator = mockk<MusicGenerator>()
    private val imageAnalyzer = mockk<ImageAnalyzer>()
    private val alarmManager = mockk<AlarmManager>(relaxed = true)

    private lateinit var albumService: AlbumService

    @AfterEach
    fun tearDown() {
        clearMocks(albumRepository, googleImageUploader, albumMemberRepository, albumMusicRepository, imageAnalyzer, musicGenerator)
    }

    @BeforeEach
    fun setUp() {
        val albumMusicManager = AlbumMusicManager(albumMusicRepository, sunoMusicDataRepository)
        albumService =
            AlbumService(
                albumManager =
                    AlbumManager(
                        albumRepository,
                        albumMemberRepository,
                        imageAnalyzer,
                        musicGenerator,
                        albumMusicManager,
                        listOf(),
                        alarmManager,
                    ),
                googleImageUploader = googleImageUploader,
                albumMusicManager = albumMusicManager,
                albumMemberManager = AlbumMemberManager(albumMemberRepository),
            )
    }

    @Test
    fun `유효하지 않은 이미지 타입이면 INVALID_IMAGE_TYPE예외가 발생한다`() {
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
        val albumId = 1L
        val memberKey = "member-key-123"
        val newAlbum = AlbumFixture.newAlbum(memberKey = memberKey, region = "오사카")
        val image = mockk<MultipartFile>()
        val entity = AlbumFixture.albumEntity(id = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { image.contentType } returns "image/jpeg"
        every { image.inputStream } returns mockk(relaxed = true)
        every { image.originalFilename } returns "test.jpg"
        every { image.resource } returns mockk(relaxed = true)
        every { googleImageUploader.uploadImage(any()) } returns "https://storage.googleapis.com/new.jpeg"
        every { albumRepository.find(albumId) } returns entity
        justRun { albumMusicRepository.deleteByAlbumId(albumId) }
        every { imageAnalyzer.analyze(any(), any(), any(), any(), any()) } returns
            ImageAnalysis("오사카의 밤", "pop style", "lyrics here")
        every { musicGenerator.generate(any(), any(), any()) } returns
            SunoMusicGenerateResponse(200, "ok", SunoMusicGenerateData("task-123"))
        every { albumMusicRepository.save(any()) } returns AlbumFixture.albumMusicEntity(albumId)

        // when
        albumService.updateAlbum(albumId, newAlbum, image)

        // then
        assertThat(entity.region).isEqualTo("오사카")
        assertThat(entity.coverImageUrl).isEqualTo("https://storage.googleapis.com/new.jpeg")
    }

    @Test
    fun `앨범 수정 시 앨범 멤버가 아니라면 NOT_ALBUM_MEMBER 예외가 발생한다`() {
        // given
        val albumId = 1L
        val memberKey = "member-key-123"
        val newAlbum = AlbumFixture.newAlbum(memberKey = memberKey)
        val image = mockk<MultipartFile>()

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns false

        // when & then
        val exception =
            assertThrows<AppException> {
                albumService.updateAlbum(albumId, newAlbum, image)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_ALBUM_MEMBER)
    }

    @Test
    fun `앨범 수정 시 앨범이 존재하지 않으면 NOT_FOUND_ALBUM 예외가 발생한다`() {
        // given
        val albumId = 1L
        val memberKey = "member-key-123"
        val newAlbum = AlbumFixture.newAlbum(memberKey = memberKey)
        val image = mockk<MultipartFile>()

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { image.contentType } returns "image/jpeg"
        every { image.inputStream } returns mockk(relaxed = true)
        every { image.originalFilename } returns "test.jpg"
        every { googleImageUploader.uploadImage(any()) } returns "https://storage.googleapis.com/new.jpeg"
        every { albumRepository.find(albumId) } returns null

        // when & then
        val exception =
            assertThrows<AppException> {
                albumService.updateAlbum(albumId, newAlbum, image)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND_ALBUM)
    }

    @Test
    fun `앨범 단건 조회 시 앨범 정보와 음악 URL이 반환된다`() {
        // given
        val albumId = 1L
        val memberKey = "member-key-123"

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumRepository.find(albumId) } returns AlbumFixture.albumEntity(1L, memberKey)
        every { albumMusicRepository.findByAlbumId(albumId) } returns AlbumFixture.albumMusicEntity(albumId)

        // when
        val result = albumService.findAlbum(albumId, memberKey)

        // then
        assertThat(result.album.albumId).isEqualTo(albumId)
        assertThat(result.musicUrl).isEqualTo("https://mock-music-url.mp3")
    }

    @Test
    fun `음악이 없는 앨범 조회 시 musicUrl이 빈 문자열로 반환된다`() {
        // given
        val albumId = 1L
        val memberKey = "member-key-123"

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumRepository.find(albumId) } returns AlbumFixture.albumEntity(1L, memberKey)
        every { albumMusicRepository.findByAlbumId(albumId) } returns null

        // when
        val result = albumService.findAlbum(albumId, memberKey)

        // then
        assertThat(result.musicUrl).isEmpty()
    }

    @Test
    fun `존재하지 않는 앨범 조회 시 NOT_FOUND_ALBUM 예외가 발생한다`() {
        // given
        val albumId = 1L
        val memberKey = "member-key-123"

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumRepository.find(albumId) } returns null

        // when & then
        val exception =
            assertThrows<AppException> {
                albumService.findAlbum(albumId, memberKey)
            }
        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND_ALBUM)
    }
}
