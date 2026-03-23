package com.vibetrip.vibetripserver.album

import com.vibetrip.vibetripserver.album.implement.AiProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.album.implement.ai.ImageAnalyzer
import com.vibetrip.vibetripserver.album.implement.ai.MusicGenerator
import com.vibetrip.vibetripserver.album.implement.ai.TitleGenerator
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AiProcessorTest :
    BehaviorSpec({
        val albumManager = mockk<AlbumManager>()
        val albumMusicManager = mockk<AlbumMusicManager>()
        val imageAnalyzer = mockk<ImageAnalyzer>()
        val titleGenerator = mockk<TitleGenerator>()
        val musicGenerator = mockk<MusicGenerator>()

        val aiProcessor =
            AiProcessor(
                albumManager = albumManager,
                albumMusicManager = albumMusicManager,
                imageAnalyzer = imageAnalyzer,
                titleGenerator = titleGenerator,
                musicGenerator = musicGenerator,
            )

        Given("AI 파이프라인을 실행하는 상황에서") {
            val albumId = 1L
            val newAlbum = AlbumFixture.newAlbum()
            val generatedMusic = AlbumFixture.generatedMusic()
            val imageKeywords = "도시, 야경, 활기찬"
            val title = "도쿄의 밤"

            When("제목 생성이 성공하면") {
                every {
                    titleGenerator.generateTitle(
                        region = newAlbum.region.value,
                        comment = newAlbum.comment.value,
                        genre = newAlbum.genre.value.name,
                        imageKeywords = imageKeywords,
                    )
                } returns title
                every { albumManager.updateTitle(albumId, title) } returns Unit

                Then("앨범 제목이 업데이트된다") {
                    aiProcessor.generateTitle(albumId, newAlbum, imageKeywords)

                    verify { albumManager.updateTitle(albumId, title) }
                }
            }

            When("앨범이 존재하지 않아 제목 업데이트가 실패하면") {
                every {
                    titleGenerator.generateTitle(
                        region = newAlbum.region.value,
                        comment = newAlbum.comment.value,
                        genre = newAlbum.genre.value.name,
                        imageKeywords = imageKeywords,
                    )
                } returns title
                every { albumManager.updateTitle(albumId, title) } throws AppException(ErrorType.NOT_FOUND_ALBUM)

                Then("AppException 예외가 발생한다") {
                    val exception =
                        shouldThrow<AppException> {
                            aiProcessor.generateTitle(albumId, newAlbum, imageKeywords)
                        }

                    exception.errorType shouldBe ErrorType.NOT_FOUND_ALBUM
                }
            }

            When("음악 생성이 성공하면") {
                every {
                    musicGenerator.generateMusic(
                        region = newAlbum.region.value,
                        comment = newAlbum.comment.value,
                        genre = newAlbum.genre.value.name,
                        genreDescription = newAlbum.genre.value.description,
                        vocalGender = newAlbum.vocalOption.vocalGender,
                        withLyrics = newAlbum.vocalOption.withLyrics,
                        imageKeywords = imageKeywords,
                    )
                } returns generatedMusic
                every { albumMusicManager.save(albumId, newAlbum, generatedMusic) } returns Unit

                Then("음악이 저장된다") {
                    aiProcessor.generateMusic(albumId, newAlbum, imageKeywords)

                    verify { albumMusicManager.save(albumId, newAlbum, generatedMusic) }
                }
            }

            When("음악 생성이 실패하면") {
                every {
                    musicGenerator.generateMusic(any(), any(), any(), any(), any(), any(), any())
                } throws AppException(ErrorType.MUSIC_GENERATE_FAILED)

                Then("예외를 삼키고 음악이 저장되지 않는다") {
                    aiProcessor.generateMusic(albumId, newAlbum, imageKeywords)

                    verify(exactly = 0) { albumMusicManager.save(any(), any(), any()) }
                }
            }
        }
    })
