package com.vibetrip.vibetripserver.albumlog.business

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageOutboxRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.albumlog.domain.event.AlbumLogImageEvent
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogImageOutboxProcessor
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogManager
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.TempFileStorage
import com.vibetrip.vibetripserver.fixture.AlbumLogFixture
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

class AlbumLogServiceTest {
    private val albumMemberRepository = mockk<AlbumMemberRepository>()
    private val albumLogRepository = mockk<AlbumLogRepository>()
    private val albumLogImageOutboxRepository = mockk<AlbumLogImageOutboxRepository>()
    private val albumLogImageRepository = mockk<AlbumLogImageRepository>()
    private val googleImageUploader = mockk<GoogleImageUploader>()
    private val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

    private lateinit var albumLogService: AlbumLogService

    @BeforeEach
    fun setUp() {
        val albumMemberManager = AlbumMemberManager(albumMemberRepository)
        val albumLogManager = AlbumLogManager(albumLogRepository, albumLogImageRepository)
        val albumLogImageOutboxProcessor =
            AlbumLogImageOutboxProcessor(
                outboxRepository = albumLogImageOutboxRepository,
                albumLogImageRepository = albumLogImageRepository,
                googleImageUploader = googleImageUploader,
            )

        albumLogService =
            AlbumLogService(
                albumMemberManager = albumMemberManager,
                albumLogManager = albumLogManager,
                eventPublisher = eventPublisher,
                albumLogImageOutboxProcessor = albumLogImageOutboxProcessor,
            )

        mockkObject(TempFileStorage)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(TempFileStorage)
        clearMocks(eventPublisher, recordedCalls = true, answers = false)
    }

    @Test
    fun `앨범 멤버이고 유효한 이미지라면 앨범 로그 ID가 반환된다`() {
        // given
        val memberKey = "member-key-123"
        val images = AlbumLogFixture.mockMultipartFiles(2)
        val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L, images = images)
        val savedAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = 1L, albumId = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumLogRepository.save(any()) } returns savedAlbumLogEntity
        every { TempFileStorage.save(any()) } returnsMany
            listOf(
                Path.of("/tmp/uploads/test1.jpg"),
                Path.of("/tmp/uploads/test2.jpg"),
            )
        every { albumLogImageOutboxRepository.save(any()) } returnsMany
            listOf(
                AlbumLogFixture.albumLogImageOutbox(id = 1L, albumLogId = 1L),
                AlbumLogFixture.albumLogImageOutbox(id = 2L, albumLogId = 1L),
            )

        // when
        val result = albumLogService.registerAlbumLog(newAlbumLog, memberKey)

        // then
        assertThat(result).isEqualTo(1L)
    }

    @Test
    fun `앨범 멤버이고 유효한 이미지라면 앨범 로그가 저장된다`() {
        // given
        val memberKey = "member-key-123"
        val images = AlbumLogFixture.mockMultipartFiles(2)
        val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L, images = images)
        val savedAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = 1L, albumId = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumLogRepository.save(any()) } returns savedAlbumLogEntity
        every { TempFileStorage.save(any()) } returnsMany
            listOf(
                Path.of("/tmp/uploads/test1.jpg"),
                Path.of("/tmp/uploads/test2.jpg"),
            )
        every { albumLogImageOutboxRepository.save(any()) } returnsMany
            listOf(
                AlbumLogFixture.albumLogImageOutbox(id = 1L, albumLogId = 1L),
                AlbumLogFixture.albumLogImageOutbox(id = 2L, albumLogId = 1L),
            )

        // when
        albumLogService.registerAlbumLog(newAlbumLog, memberKey)

        // then
        verify { albumLogRepository.save(any()) }
    }

    @Test
    fun `앨범 멤버이고 유효한 이미지라면 이미지 Outbox가 저장된다`() {
        // given
        val memberKey = "member-key-123"
        val images = AlbumLogFixture.mockMultipartFiles(2)
        val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L, images = images)
        val savedAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = 1L, albumId = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumLogRepository.save(any()) } returns savedAlbumLogEntity
        every { TempFileStorage.save(any()) } returnsMany
            listOf(
                Path.of("/tmp/uploads/test1.jpg"),
                Path.of("/tmp/uploads/test2.jpg"),
            )
        every { albumLogImageOutboxRepository.save(any()) } returnsMany
            listOf(
                AlbumLogFixture.albumLogImageOutbox(id = 1L, albumLogId = 1L),
                AlbumLogFixture.albumLogImageOutbox(id = 2L, albumLogId = 1L),
            )

        // when
        albumLogService.registerAlbumLog(newAlbumLog, memberKey)

        // then
        verify(exactly = 2) { albumLogImageOutboxRepository.save(any()) }
    }

    @Test
    fun `앨범 멤버이고 유효한 이미지라면 이벤트가 발행된다`() {
        // given
        val memberKey = "member-key-123"
        val images = AlbumLogFixture.mockMultipartFiles(2)
        val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L, images = images)
        val savedAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = 1L, albumId = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumLogRepository.save(any()) } returns savedAlbumLogEntity
        every { TempFileStorage.save(any()) } returnsMany
            listOf(
                Path.of("/tmp/uploads/test1.jpg"),
                Path.of("/tmp/uploads/test2.jpg"),
            )
        every { albumLogImageOutboxRepository.save(any()) } returnsMany
            listOf(
                AlbumLogFixture.albumLogImageOutbox(id = 1L, albumLogId = 1L),
                AlbumLogFixture.albumLogImageOutbox(id = 2L, albumLogId = 1L),
            )

        // when
        albumLogService.registerAlbumLog(newAlbumLog, memberKey)

        // then
        val eventSlot = slot<AlbumLogImageEvent>()
        verify { eventPublisher.publishEvent(capture(eventSlot)) }

        assertThat(eventSlot.captured.albumLogId).isEqualTo(1L)
        assertThat(eventSlot.captured.outboxIds).isEqualTo(listOf(1L, 2L))
    }

    @Test
    fun `앨범 멤버가 아니라면 NOT_ALBUM_MEMBER 예외가 발생한다`() {
        // given
        val memberKey = "member-key-123"
        val images = AlbumLogFixture.mockMultipartFiles(2)
        val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L, images = images)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns false

        // when & then
        val exception =
            assertThrows<AppException> {
                albumLogService.registerAlbumLog(newAlbumLog, memberKey)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_ALBUM_MEMBER)
    }

    @Test
    fun `유효하지 않은 이미지 타입이라면 INVALID_IMAGE_TYPE 예외가 발생한다`() {
        // given
        val memberKey = "member-key-123"
        val invalidImages =
            listOf(
                AlbumLogFixture.mockMultipartFile(contentType = "application/pdf"),
            )
        val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L, images = invalidImages)
        val savedAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = 1L, albumId = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumLogRepository.save(any()) } returns savedAlbumLogEntity

        // when & then
        val exception =
            assertThrows<AppException> {
                albumLogService.registerAlbumLog(newAlbumLog, memberKey)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.INVALID_IMAGE_TYPE)
    }

    @Test
    fun `이미지 없이 앨범 로그를 등록하면 앨범 로그 ID가 반환된다`() {
        // given
        val memberKey = "member-key-123"
        val emptyImages = emptyList<MultipartFile>()
        val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L, images = emptyImages)
        val savedAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = 1L, albumId = 1L)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
        every { albumLogRepository.save(any()) } returns savedAlbumLogEntity

        // when
        val result = albumLogService.registerAlbumLog(newAlbumLog, memberKey)

        // then
        assertThat(result).isEqualTo(1L)
    }

    @Test
    fun `앨범 멤버이고 앨범 로그가 존재하면 앨범 로그 목록이 반환된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val cursorable = Cursorable<Long>(cursor = null, limit = 10)
        val albumLogEntities =
            listOf(
                AlbumLogFixture.albumLogEntity(id = 2L, albumId = albumId),
                AlbumLogFixture.albumLogEntity(id = 1L, albumId = albumId),
            )
        val albumLogImages =
            listOf(
                AlbumLogFixture.albumLogImageEntity(id = 1L, albumLogId = 2L),
                AlbumLogFixture.albumLogImageEntity(id = 2L, albumLogId = 2L),
                AlbumLogFixture.albumLogImageEntity(id = 3L, albumLogId = 1L),
            )

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogRepository.findByAlbumId(albumId, cursorable) } returns
            Slice(albumLogEntities, cursorable, hasNext = false)
        every { albumLogImageRepository.findByAlbumLogIds(listOf(2L, 1L)) } returns albumLogImages

        // when
        val result = albumLogService.findAlbumLogs(albumId, cursorable, memberKey)

        // then
        assertThat(result.content.size).isEqualTo(2)
        assertThat(result.hasNext).isEqualTo(false)
    }

    @Test
    fun `앨범 멤버이고 앨범 로그가 존재하면 각 앨범 로그에 이미지가 포함된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val cursorable = Cursorable<Long>(cursor = null, limit = 10)
        val albumLogEntities =
            listOf(
                AlbumLogFixture.albumLogEntity(id = 2L, albumId = albumId),
                AlbumLogFixture.albumLogEntity(id = 1L, albumId = albumId),
            )
        val albumLogImages =
            listOf(
                AlbumLogFixture.albumLogImageEntity(id = 1L, albumLogId = 2L),
                AlbumLogFixture.albumLogImageEntity(id = 2L, albumLogId = 2L),
                AlbumLogFixture.albumLogImageEntity(id = 3L, albumLogId = 1L),
            )

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogRepository.findByAlbumId(albumId, cursorable) } returns
            Slice(albumLogEntities, cursorable, hasNext = false)
        every { albumLogImageRepository.findByAlbumLogIds(listOf(2L, 1L)) } returns albumLogImages

        // when
        val result = albumLogService.findAlbumLogs(albumId, cursorable, memberKey)

        // then
        assertThat(result.content[0].albumLogImages.size).isEqualTo(2)
        assertThat(result.content[1].albumLogImages.size).isEqualTo(1)
    }

    @Test
    fun `앨범 멤버이고 앨범 로그가 없으면 빈 목록이 반환된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val cursorable = Cursorable<Long>(cursor = null, limit = 10)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogRepository.findByAlbumId(albumId, cursorable) } returns
            Slice(emptyList(), cursorable, hasNext = false)
        every { albumLogImageRepository.findByAlbumLogIds(emptyList()) } returns emptyList()

        // when
        val result = albumLogService.findAlbumLogs(albumId, cursorable, memberKey)

        // then
        assertThat(result.content.size).isEqualTo(0)
        assertThat(result.hasNext).isEqualTo(false)
    }

    @Test
    fun `앨범 로그 목록 조회 시 앨범 멤버가 아니라면 NOT_ALBUM_MEMBER 예외가 발생한다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val cursorable = Cursorable<Long>(cursor = null, limit = 10)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns false

        // when & then
        val exception =
            assertThrows<AppException> {
                albumLogService.findAlbumLogs(albumId, cursorable, memberKey)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_ALBUM_MEMBER)
    }

    @Test
    fun `앨범 멤버이고 새 이미지와 삭제할 이미지가 있으면 기존 이미지가 삭제된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L
        val newImages = AlbumLogFixture.mockMultipartFiles(2)
        val removeImageIds = listOf(1L, 2L)
        val editAlbumLog =
            AlbumLogFixture.editAlbumLog(
                id = albumLogId,
                albumId = albumId,
                newImages = newImages,
                removeImageIds = removeImageIds,
            )
        val existingAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = albumLogId, albumId = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogImageRepository.deleteByIds(removeImageIds) } returns Unit
        every { albumLogRepository.find(albumLogId) } returns existingAlbumLogEntity
        every { TempFileStorage.save(any()) } returnsMany
            listOf(
                Path.of("/tmp/uploads/test1.jpg"),
                Path.of("/tmp/uploads/test2.jpg"),
            )
        every { albumLogImageOutboxRepository.save(any()) } returnsMany
            listOf(
                AlbumLogFixture.albumLogImageOutbox(id = 3L, albumLogId = albumLogId),
                AlbumLogFixture.albumLogImageOutbox(id = 4L, albumLogId = albumLogId),
            )

        // when
        albumLogService.updateAlbumLog(editAlbumLog, memberKey)

        // then
        verify { albumLogImageRepository.deleteByIds(removeImageIds) }
    }

    @Test
    fun `앨범 멤버이고 새 이미지와 삭제할 이미지가 있으면 새 이미지 Outbox가 저장된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L
        val newImages = AlbumLogFixture.mockMultipartFiles(2)
        val removeImageIds = listOf(1L, 2L)
        val editAlbumLog =
            AlbumLogFixture.editAlbumLog(
                id = albumLogId,
                albumId = albumId,
                newImages = newImages,
                removeImageIds = removeImageIds,
            )
        val existingAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = albumLogId, albumId = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogImageRepository.deleteByIds(removeImageIds) } returns Unit
        every { albumLogRepository.find(albumLogId) } returns existingAlbumLogEntity
        every { TempFileStorage.save(any()) } returnsMany
            listOf(
                Path.of("/tmp/uploads/test1.jpg"),
                Path.of("/tmp/uploads/test2.jpg"),
            )
        every { albumLogImageOutboxRepository.save(any()) } returnsMany
            listOf(
                AlbumLogFixture.albumLogImageOutbox(id = 3L, albumLogId = albumLogId),
                AlbumLogFixture.albumLogImageOutbox(id = 4L, albumLogId = albumLogId),
            )

        // when
        albumLogService.updateAlbumLog(editAlbumLog, memberKey)

        // then
        verify(exactly = 2) { albumLogImageOutboxRepository.save(any()) }
    }

    @Test
    fun `앨범 멤버이고 새 이미지와 삭제할 이미지가 있으면 이벤트가 발행된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L
        val newImages = AlbumLogFixture.mockMultipartFiles(2)
        val removeImageIds = listOf(1L, 2L)
        val editAlbumLog =
            AlbumLogFixture.editAlbumLog(
                id = albumLogId,
                albumId = albumId,
                newImages = newImages,
                removeImageIds = removeImageIds,
            )
        val existingAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = albumLogId, albumId = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogImageRepository.deleteByIds(removeImageIds) } returns Unit
        every { albumLogRepository.find(albumLogId) } returns existingAlbumLogEntity
        every { TempFileStorage.save(any()) } returnsMany
            listOf(
                Path.of("/tmp/uploads/test1.jpg"),
                Path.of("/tmp/uploads/test2.jpg"),
            )
        every { albumLogImageOutboxRepository.save(any()) } returnsMany
            listOf(
                AlbumLogFixture.albumLogImageOutbox(id = 3L, albumLogId = albumLogId),
                AlbumLogFixture.albumLogImageOutbox(id = 4L, albumLogId = albumLogId),
            )

        // when
        albumLogService.updateAlbumLog(editAlbumLog, memberKey)

        // then
        val eventSlot = slot<AlbumLogImageEvent>()
        verify { eventPublisher.publishEvent(capture(eventSlot)) }

        assertThat(eventSlot.captured.albumLogId).isEqualTo(albumLogId)
        assertThat(eventSlot.captured.outboxIds).isEqualTo(listOf(3L, 4L))
    }

    @Test
    fun `앨범 멤버이고 새 이미지와 삭제할 이미지가 있으면 앨범 로그 설명이 수정된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L
        val newImages = AlbumLogFixture.mockMultipartFiles(2)
        val removeImageIds = listOf(1L, 2L)
        val editAlbumLog =
            AlbumLogFixture.editAlbumLog(
                id = albumLogId,
                albumId = albumId,
                newImages = newImages,
                removeImageIds = removeImageIds,
            )
        val existingAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = albumLogId, albumId = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogImageRepository.deleteByIds(removeImageIds) } returns Unit
        every { albumLogRepository.find(albumLogId) } returns existingAlbumLogEntity
        every { TempFileStorage.save(any()) } returnsMany
            listOf(
                Path.of("/tmp/uploads/test1.jpg"),
                Path.of("/tmp/uploads/test2.jpg"),
            )
        every { albumLogImageOutboxRepository.save(any()) } returnsMany
            listOf(
                AlbumLogFixture.albumLogImageOutbox(id = 3L, albumLogId = albumLogId),
                AlbumLogFixture.albumLogImageOutbox(id = 4L, albumLogId = albumLogId),
            )

        // when
        albumLogService.updateAlbumLog(editAlbumLog, memberKey)

        // then
        assertThat(existingAlbumLogEntity.description).isEqualTo("수정된 설명")
    }

    @Test
    fun `앨범 멤버이고 이미지 변경 없이 설명만 수정하면 앨범 로그 설명이 수정된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L
        val editAlbumLog =
            AlbumLogFixture.editAlbumLog(
                id = albumLogId,
                description = "새로운 설명",
                albumId = albumId,
            )
        val existingAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = albumLogId, albumId = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogImageRepository.deleteByIds(emptyList()) } returns Unit
        every { albumLogRepository.find(albumLogId) } returns existingAlbumLogEntity

        // when
        albumLogService.updateAlbumLog(editAlbumLog, memberKey)

        // then
        assertThat(existingAlbumLogEntity.description).isEqualTo("새로운 설명")
    }

    @Test
    fun `앨범 멤버이고 이미지 변경 없이 설명만 수정하면 이미지 삭제가 호출되지만 빈 리스트다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L
        val editAlbumLog =
            AlbumLogFixture.editAlbumLog(
                id = albumLogId,
                description = "새로운 설명",
                albumId = albumId,
            )
        val existingAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = albumLogId, albumId = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogImageRepository.deleteByIds(emptyList()) } returns Unit
        every { albumLogRepository.find(albumLogId) } returns existingAlbumLogEntity

        // when
        albumLogService.updateAlbumLog(editAlbumLog, memberKey)

        // then
        verify { albumLogImageRepository.deleteByIds(emptyList()) }
    }

    @Test
    fun `앨범 로그 수정 시 앨범 멤버가 아니라면 NOT_ALBUM_MEMBER 예외가 발생한다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L
        val editAlbumLog = AlbumLogFixture.editAlbumLog(id = albumLogId, albumId = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns false

        // when & then
        val exception =
            assertThrows<AppException> {
                albumLogService.updateAlbumLog(editAlbumLog, memberKey)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_ALBUM_MEMBER)
    }

    @Test
    fun `앨범 로그 수정 시 존재하지 않는 앨범 로그라면 NOT_FOUND_DATA 예외가 발생한다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val editAlbumLog = AlbumLogFixture.editAlbumLog(id = 999L, albumId = albumId)

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogImageRepository.deleteByIds(emptyList()) } returns Unit
        every { albumLogRepository.find(999L) } returns null

        // when & then
        val exception =
            assertThrows<AppException> {
                albumLogService.updateAlbumLog(editAlbumLog, memberKey)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND_DATA)
    }

    @Test
    fun `앨범 멤버이면 앨범 로그가 삭제된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogRepository.delete(albumLogId) } returns Unit
        every { albumLogImageRepository.deleteByAlbumLogId(albumLogId) } returns Unit

        // when
        albumLogService.deleteAlbumLog(albumId, albumLogId, memberKey)

        // then
        verify { albumLogRepository.delete(albumLogId) }
    }

    @Test
    fun `앨범 멤버이면 앨범 로그 삭제 시 이미지도 함께 삭제된다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns true
        every { albumLogRepository.delete(albumLogId) } returns Unit
        every { albumLogImageRepository.deleteByAlbumLogId(albumLogId) } returns Unit

        // when
        albumLogService.deleteAlbumLog(albumId, albumLogId, memberKey)

        // then
        verify { albumLogImageRepository.deleteByAlbumLogId(albumLogId) }
    }

    @Test
    fun `앨범 로그 삭제 시 앨범 멤버가 아니라면 NOT_ALBUM_MEMBER 예외가 발생한다`() {
        // given
        val memberKey = "member-key-123"
        val albumId = 1L
        val albumLogId = 1L

        every { albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey) } returns false

        // when & then
        val exception =
            assertThrows<AppException> {
                albumLogService.deleteAlbumLog(albumId, albumLogId, memberKey)
            }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_ALBUM_MEMBER)
    }
}
