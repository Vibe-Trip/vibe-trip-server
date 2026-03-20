package com.vibetrip.vibetripserver.album

import com.vibetrip.vibetripserver.album.business.AlbumMusicService
import com.vibetrip.vibetripserver.album.business.AlbumService
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.presentation.dto.AlbumCreateResponse
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AlbumServiceTest :
    BehaviorSpec({
        val albumManager = mockk<AlbumManager>()
        val albumMusicService = mockk<AlbumMusicService>()

        val albumService =
            AlbumService(
                albumManager = albumManager,
                albumMusicService = albumMusicService,
            )

        Given("앨범 생성을 요청하는 상황에서") {
            val newAlbum = AlbumFixture.newAlbum()
            val albumId = 1L

            When("정상적으로 앨범을 생성하면") {
                every { albumManager.create(newAlbum) } returns albumId
                every { albumMusicService.processAlbumMusic(any<NewAlbumMusic>()) } returns Unit

                Then("albumId가 담긴 AlbumCreateResponse를 반환한다") {
                    val result = albumService.create(newAlbum, null)

                    result shouldBe AlbumCreateResponse(albumId = albumId)
                    verify { albumManager.create(newAlbum) }
                    verify { albumMusicService.processAlbumMusic(any()) }
                }
            }
        }
    })
