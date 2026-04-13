package com.vibetrip.vibetripserver.alarm.business

import com.vibetrip.vibetripserver.alarm.implement.AlarmManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class AlarmService(
    private val alarmManager: AlarmManager,
) {
    fun findAlarms(memberKey: String) = alarmManager.findAll(memberKey)

    @Transactional
    fun deleteAlarm(
        alarmId: Long,
        memberKey: String,
    ) {
        alarmManager.delete(alarmId, memberKey)
    }
}
