package com.vibetrip.vibetripserver.alarm.dataaccess.repository

import com.vibetrip.vibetripserver.alarm.dataaccess.entity.AlarmEntity

interface CustomAlarmRepository {
    fun findByMemberKey(memberKey: String): List<AlarmEntity>

    fun delete(
        memberKey: String,
        alarmId: Long,
    )
}
