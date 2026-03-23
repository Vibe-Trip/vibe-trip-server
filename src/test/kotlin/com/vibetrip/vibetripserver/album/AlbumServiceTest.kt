package com.vibetrip.vibetripserver.album

import com.vibetrip.vibetripserver.album.business.AlbumService
import com.vibetrip.vibetripserver.album.implement.AiProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumCreateResponse
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.multipart.MultipartFile

class AlbumServiceTest :
    BehaviorSpec({
        val albumManager = mockk<AlbumManager>()
        val aiProcessor = mockk<AiProcessor>()

        val albumService =
            AlbumService(
                albumManager = albumManager,
                aiProcessor = aiProcessor,
            )

        Given("앨범 생성을 요청하는 상황에서") {
            val newAlbum = AlbumFixture.newAlbum()
            val image = mockk<MultipartFile>()
            val albumId = 1L
            val imageKeywords = ""

            When("정상적으로 앨범을 생성하면") {
                every { albumManager.create(newAlbum, "") } returns albumId
                every { aiProcessor.analyzeImage("") } returns imageKeywords
                every { aiProcessor.generateTitle(albumId, newAlbum, imageKeywords) } returns Unit
                every { aiProcessor.generateMusic(albumId, newAlbum, imageKeywords) } returns Unit

                Then("albumId가 담긴 AlbumCreateResponse를 반환한다") {
                    val result = albumService.create(newAlbum, image)

                    result shouldBe AlbumCreateResponse(albumId = albumId)
                    verify { albumManager.create(newAlbum, "") }
                    verify { aiProcessor.analyzeImage("") }
                    verify { aiProcessor.generateTitle(albumId, newAlbum, imageKeywords) }
                    verify { aiProcessor.generateMusic(albumId, newAlbum, imageKeywords) }
                }
            }

            When("앨범 저장에 실패하면") {
                every { albumManager.create(newAlbum, "") } throws AppException(ErrorType.SERVER_ERROR)

                Then("AppException 예외가 발생한다") {
                    val exception =
                        shouldThrow<AppException> {
                            albumService.create(newAlbum, image)
                        }

                    exception.errorType shouldBe ErrorType.SERVER_ERROR
                }
            }
        }
    })
