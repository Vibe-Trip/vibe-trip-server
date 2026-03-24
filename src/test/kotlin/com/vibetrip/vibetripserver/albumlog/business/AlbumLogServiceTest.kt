package com.vibetrip.vibetripserver.albumlog.business

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageOutboxRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.albumlog.domain.event.AlbumLogImageEvent
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogCounter
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogImageOutboxProcessor
import com.vibetrip.vibetripserver.albumlog.implement.AlbumLogManager
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.TempFileStorage
import com.vibetrip.vibetripserver.fixture.AlbumLogFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

class AlbumLogServiceTest :
    BehaviorSpec(
        {
            val albumMemberRepository = mockk<AlbumMemberRepository>()
            val albumLogRepository = mockk<AlbumLogRepository>()
            val albumLogImageOutboxRepository = mockk<AlbumLogImageOutboxRepository>()
            val albumLogImageRepository = mockk<AlbumLogImageRepository>()
            val googleImageUploader = mockk<GoogleImageUploader>()
            val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
            val albumLogConter = mockk<AlbumLogCounter>()

            val albumMemberManager = AlbumMemberManager(albumMemberRepository)
            val albumLogManager = AlbumLogManager(albumLogRepository)
            val albumLogImageOutboxProcessor =
                AlbumLogImageOutboxProcessor(
                    outboxRepository = albumLogImageOutboxRepository,
                    albumLogImageRepository = albumLogImageRepository,
                    googleImageUploader = googleImageUploader,
                )

            val albumLogService =
                AlbumLogService(
                    albumMemberManager = albumMemberManager,
                    albumLogManager = albumLogManager,
                    eventPublisher = eventPublisher,
                    albumLogImageOutboxProcessor = albumLogImageOutboxProcessor,
                    albumLogCounter = albumLogConter,
                )

            beforeSpec {
                mockkObject(TempFileStorage)
            }

            afterSpec {
                unmockkObject(TempFileStorage)
            }

            Given("앨범 로그를 등록하는 상황에서") {
                val memberKey = "member-key-123"
                val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L)
                val images = AlbumLogFixture.mockMultipartFiles(2)
                val savedAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = 1L, albumId = 1L)

                When("앨범 멤버이고 유효한 이미지라면") {
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

                    val result = albumLogService.registerAlbumLog(newAlbumLog, images, memberKey)

                    Then("앨범 로그 ID가 반환된다") {
                        result shouldBe 1L
                    }

                    Then("앨범 로그가 저장된다") {
                        verify { albumLogRepository.save(any()) }
                    }

                    Then("이미지 Outbox가 저장된다") {
                        verify(exactly = 2) { albumLogImageOutboxRepository.save(any()) }
                    }

                    Then("이벤트가 발행된다") {
                        val eventSlot = slot<AlbumLogImageEvent>()
                        verify { eventPublisher.publishEvent(capture(eventSlot)) }

                        eventSlot.captured.albumLogId shouldBe 1L
                        eventSlot.captured.outboxIds shouldBe listOf(1L, 2L)
                    }
                }

                When("앨범 멤버가 아니라면") {
                    every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns false

                    Then("NOT_ALBUM_MEMBER 예외가 발생한다") {
                        val exception =
                            shouldThrow<AppException> {
                                albumLogService.registerAlbumLog(newAlbumLog, images, memberKey)
                            }

                        exception.errorType shouldBe ErrorType.NOT_ALBUM_MEMBER
                    }
                }

                When("유효하지 않은 이미지 타입이라면") {
                    val invalidImages =
                        listOf(
                            AlbumLogFixture.mockMultipartFile(contentType = "application/pdf"),
                        )

                    every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
                    every { albumLogRepository.save(any()) } returns savedAlbumLogEntity

                    Then("INVALID_IMAGE_TYPE 예외가 발생한다") {
                        val exception =
                            shouldThrow<AppException> {
                                albumLogService.registerAlbumLog(newAlbumLog, invalidImages, memberKey)
                            }

                        exception.errorType shouldBe ErrorType.INVALID_IMAGE_TYPE
                    }
                }
            }

            Given("이미지 없이 앨범 로그를 등록하는 상황에서") {
                val memberKey = "member-key-123"
                val newAlbumLog = AlbumLogFixture.newAlbumLog(albumId = 1L)
                val emptyImages = emptyList<MultipartFile>()
                val savedAlbumLogEntity = AlbumLogFixture.albumLogEntity(id = 1L, albumId = 1L)

                When("앨범 멤버라면") {
                    every { albumMemberRepository.existsByAlbumIdAndMemberKey(1L, memberKey) } returns true
                    every { albumLogRepository.save(any()) } returns savedAlbumLogEntity

                    val result = albumLogService.registerAlbumLog(newAlbumLog, emptyImages, memberKey)

                    Then("앨범 로그 ID가 반환된다") {
                        result shouldBe 1L
                    }
                }
            }
        },
    )
