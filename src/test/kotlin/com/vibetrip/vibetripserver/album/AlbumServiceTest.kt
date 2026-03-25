package com.vibetrip.vibetripserver.album

import com.vibetrip.vibetripserver.album.business.AlbumService
import com.vibetrip.vibetripserver.album.implement.AiProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumCreateResponse
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.multipart.MultipartFile

class AlbumServiceTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

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
            val imageKeywords = "도시, 야경, 활기찬"

            When("모든 단계가 정상적으로 성공하면") {
                every { albumManager.create(newAlbum, any()) } returns albumId
                every { aiProcessor.analyzeImage(any()) } returns imageKeywords
                justRun { aiProcessor.generateTitle(albumId, newAlbum, imageKeywords) }
                justRun { aiProcessor.generateMusic(albumId, newAlbum, imageKeywords) }

                Then("albumId가 담긴 AlbumCreateResponse를 반환하고 모든 단계가 순서대로 호출된다") {
                    val result = albumService.create(newAlbum, image)

                    result shouldBe AlbumCreateResponse(albumId = albumId)
                    // 반환값 검증 외에 각 단계 호출 여부도 확인
                    verify(exactly = 1) { albumManager.create(newAlbum, any()) }
                    verify(exactly = 1) { aiProcessor.analyzeImage(any()) }
                    verify(exactly = 1) { aiProcessor.generateTitle(albumId, newAlbum, imageKeywords) }
                    verify(exactly = 1) { aiProcessor.generateMusic(albumId, newAlbum, imageKeywords) }
                }
            }

            When("앨범 저장에 실패하면") {
                every { albumManager.create(any(), any()) } throws AppException(ErrorType.SERVER_ERROR)

                Then("AppException이 전파되고 이후 AI 처리는 실행되지 않는다") {
                    val exception =
                        shouldThrow<AppException> {
                            albumService.create(newAlbum, image)
                        }

                    exception.errorType shouldBe ErrorType.SERVER_ERROR
                    // 조기 실패 → 하위 단계가 실행되지 않아야 함
                    verify(exactly = 0) { aiProcessor.analyzeImage(any()) }
                    verify(exactly = 0) { aiProcessor.generateTitle(any(), any(), any()) }
                    verify(exactly = 0) { aiProcessor.generateMusic(any(), any(), any()) }
                }
            }

            When("이미지 분석(analyzeImage)에 실패하면") {
                every { albumManager.create(newAlbum, any()) } returns albumId
                every { aiProcessor.analyzeImage(any()) } throws AppException(ErrorType.SERVER_ERROR)

                Then("AppException이 전파되고 제목/음악 생성은 실행되지 않는다") {
                    val exception =
                        shouldThrow<AppException> {
                            albumService.create(newAlbum, image)
                        }

                    exception.errorType shouldBe ErrorType.SERVER_ERROR
                    verify(exactly = 0) { aiProcessor.generateTitle(any(), any(), any()) }
                    verify(exactly = 0) { aiProcessor.generateMusic(any(), any(), any()) }
                }
            }
        }
    })
