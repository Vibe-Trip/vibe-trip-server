package com.vibetrip.vibetripserver.alarm.business

import com.vibetrip.vibetripserver.alarm.dataaccess.repository.AlarmRepository
import com.vibetrip.vibetripserver.alarm.domain.AlarmData
import com.vibetrip.vibetripserver.alarm.domain.AlarmType
import com.vibetrip.vibetripserver.alarm.implement.AlarmManager
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AlarmServiceTest : SpringTest() {
    @Autowired
    lateinit var alarmService: AlarmService

    @Autowired
    lateinit var alarmManager: AlarmManager

    @Autowired
    lateinit var alarmRepository: AlarmRepository

    private val memberKey = "test-member-key"

    @AfterEach
    fun tearDown() {
        alarmRepository.deleteAll()
    }

    @Test
    fun `알림 여러 개를 저장하면 전체 조회가 된다`() {
        // given
        alarmManager.send(memberKey, AlarmData.Creating(albumId = 1L, taskId = "task-1"))
        alarmManager.send(memberKey, AlarmData.Completed(albumId = 1L, albumTitle = "도쿄의 밤"))
        alarmManager.send(memberKey, AlarmData.Failed(albumId = 1L, errorType = ErrorType.MUSIC_GENERATE_FAILED))

        // when
        val alarms = alarmService.findAlarms(memberKey)

        // then
        assertThat(alarms).hasSize(3)
        assertThat(alarms.map { it.alarmType }).containsExactlyInAnyOrder(
            AlarmType.CREATING,
            AlarmType.COMPLETED,
            AlarmType.FAILED,
        )
    }

    @Test
    fun `다른 멤버의 알림은 조회되지 않는다`() {
        // given
        alarmManager.send(memberKey, AlarmData.Creating(albumId = 1L, taskId = "task-1"))
        alarmManager.send("other-member-key", AlarmData.Completed(albumId = 2L, albumTitle = "오사카의 낮"))

        // when
        val alarms = alarmService.findAlarms(memberKey)

        // then
        assertThat(alarms).hasSize(1)
        assertThat(alarms.first().alarmType).isEqualTo(AlarmType.CREATING)
    }

    @Test
    fun `알림을 삭제하면 조회되지 않는다`() {
        // given
        alarmManager.send(memberKey, AlarmData.Creating(albumId = 1L, taskId = "task-1"))
        alarmManager.send(memberKey, AlarmData.Completed(albumId = 1L, albumTitle = "도쿄의 밤"))
        val alarmId = alarmService.findAlarms(memberKey).first().id!!

        // when
        alarmService.deleteAlarm(alarmId, memberKey)

        // then
        assertThat(alarmService.findAlarms(memberKey)).hasSize(1)
    }

    @Test
    fun `다른 멤버의 알림은 삭제되지 않는다`() {
        // given
        alarmManager.send(memberKey, AlarmData.Creating(albumId = 1L, taskId = "task-1"))
        val alarmId = alarmService.findAlarms(memberKey).first().id!!

        // when
        alarmService.deleteAlarm(alarmId, "other-member-key")

        // then
        assertThat(alarmService.findAlarms(memberKey)).hasSize(1)
    }

    @Test
    fun `albumId가 알림에 저장된다`() {
        // given
        val albumId = 42L
        alarmManager.send(memberKey, AlarmData.Completed(albumId = albumId, albumTitle = "도쿄의 밤"))

        // when
        val alarm = alarmService.findAlarms(memberKey).first()

        // then
        assertThat(alarm.albumId).isEqualTo(albumId)
    }

    @Test
    fun `Completed 알림 description에 앨범 제목이 포함된다`() {
        // given
        alarmManager.send(memberKey, AlarmData.Completed(albumId = 1L, albumTitle = "도쿄의 밤"))

        // when
        val alarm = alarmService.findAlarms(memberKey).first()

        // then
        assertThat(alarm.description).contains("도쿄의 밤")
    }

    @Test
    fun `Failed 알림 description에 에러 메시지가 포함된다`() {
        // given
        alarmManager.send(memberKey, AlarmData.Failed(albumId = 1L, errorType = ErrorType.MUSIC_GENERATE_FAILED))

        // when
        val alarm = alarmService.findAlarms(memberKey).first()

        // then
        assertThat(alarm.description).contains(ErrorType.MUSIC_GENERATE_FAILED.message)
    }
}
