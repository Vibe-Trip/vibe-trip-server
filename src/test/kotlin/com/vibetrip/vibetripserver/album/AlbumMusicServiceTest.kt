package com.vibetrip.vibetripserver.album

import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.album.implement.ai.MusicGenerator
import com.vibetrip.vibetripserver.album.implement.ai.TitleGenerator
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AlbumMusicServiceTest :
    BehaviorSpec({
        val albumManager = mockk<AlbumManager>()
        val albumMusicManager = mockk<AlbumMusicManager>()
        val musicGenerator = mockk<MusicGenerator>()
        val titleGenerator = mockk<TitleGenerator>()

        val albumMusicService =
            AlbumMusicService(
                albumManager = albumManager,
                albumMusicManager = albumMusicManager,
                musicGenerator = musicGenerator,
                titleGenerator = titleGenerator,
            )

        Given("AI 파이프라인을 실행하는 상황에서") {
            val albumId = 1L
            val newAlbum = AlbumFixture.newAlbum()
            val generatedMusic = AlbumFixture.generatedMusic()
            val title = "도쿄의 밤"

            When("제목과 음악 생성이 모두 성공하면") {
                every { titleGenerator.generateTitle(newAlbum) } returns title
                every { albumManager.updateTitle(albumId, title) } returns Unit
                every { musicGenerator.generateMusic(newAlbum) } returns generatedMusic
                every { albumMusicManager.save(albumId, newAlbum, generatedMusic) } returns Unit

                Then("제목 업데이트 후 음악이 저장된다") {
                    albumMusicService.processAlbumMusic(albumId, newAlbum)

                    verify { titleGenerator.generateTitle(newAlbum) }
                    verify { albumManager.updateTitle(albumId, title) }
                    verify { musicGenerator.generateMusic(newAlbum) }
                    verify { albumMusicManager.save(albumId, newAlbum, generatedMusic) }
                }
            }

            When("앨범이 존재하지 않으면") {
                every { titleGenerator.generateTitle(newAlbum) } returns title
                every {
                    albumManager.updateTitle(albumId, title)
                } throws AppException(ErrorType.NOT_FOUND_ALBUM)

                Then("예외를 삼키고 정상 종료된다") {
                    albumMusicService.processAlbumMusic(albumId, newAlbum)
                }
            }

            When("음악 생성이 실패하면") {
                every { titleGenerator.generateTitle(newAlbum) } returns title
                every { albumManager.updateTitle(albumId, title) } returns Unit
                every { musicGenerator.generateMusic(newAlbum) } throws AppException(ErrorType.MUSIC_GENERATE_FAILED)

                Then("예외를 삼키고 정상 종료된다") {
                    albumMusicService.processAlbumMusic(albumId, newAlbum)
                }
            }
        }
    })
