package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.alarm.dataaccess.repository.AlarmRepository
import com.vibetrip.vibetripserver.alarm.domain.AlarmType
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.SunoMusicData
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.fixture.AlbumFixture
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AlbumAlarmFlowTest : SpringTest() {
    @Autowired
    lateinit var albumService: AlbumService

    @Autowired
    lateinit var albumRepository: AlbumRepository

    @Autowired
    lateinit var albumMusicRepository: AlbumMusicRepository

    @Autowired
    lateinit var alarmRepository: AlarmRepository

    @AfterEach
    fun tearDown() {
        alarmRepository.deleteAll()
        albumMusicRepository.deleteAll()
        albumRepository.deleteAll()
    }

    @Test
    fun `Suno 콜백 수신 시 Completed 알림이 DB에 저장된다`() {
        // given
        val memberKey = "test-member-key"
        val taskId = "suno-task-123"

        val album = albumRepository.save(AlbumFixture.albumEntity(memberKey = memberKey, title = "도쿄의 밤"))
        albumMusicRepository.save(
            AlbumMusicEntity(
                title = "도쿄의 밤",
                musicUrl = "",
                genre = GenreType.LO_FI,
                withLyrics = false,
                vocalGender = VocalGender.N,
                albumId = album.id!!,
                taskId = taskId,
            ),
        )

        // when
        albumService.updateMusic(sunoMusicData(taskId))

        // then
        val alarms = alarmRepository.findAll()
        assertThat(alarms).hasSize(1)
        assertThat(alarms.first().alarmType).isEqualTo(AlarmType.COMPLETED)
        assertThat(alarms.first().albumId).isEqualTo(album.id)
        assertThat(alarms.first().memberKey).isEqualTo(memberKey)
    }

    private fun sunoMusicData(taskId: String) =
        SunoMusicData(
            taskId = taskId,
            id = "suno-id",
            audioUrl = "https://suno.com/audio.mp3",
            sourceAudioUrl = "",
            streamAudioUrl = "",
            sourceStreamAudioUrl = "",
            imageUrl = "",
            sourceImageUrl = "",
            prompt = "test prompt",
            modelName = "chirp-v3",
            title = "도쿄의 밤",
            tags = "pop",
            createTime = "2026-04-01",
            duration = 180.0,
            callbackType = "complete",
        )
}
