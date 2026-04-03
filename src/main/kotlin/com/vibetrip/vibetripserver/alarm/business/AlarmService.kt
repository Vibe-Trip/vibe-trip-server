package com.vibetrip.vibetripserver.alarm.business

import com.vibetrip.vibetripserver.alarm.implement.AlarmManager
import com.vibetrip.vibetripserver.alarm.presentation.dto.response.AlarmResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class AlarmService(
    private val alarmManager: AlarmManager,
) {
    fun findAlarms(memberKey: String): List<AlarmResponse> =
        alarmManager.findAll(memberKey).map { AlarmResponse.from(it) }

    @Transactional
    fun deleteAlarm(alarmId: Long) {
        alarmManager.delete(alarmId)
    }
}
