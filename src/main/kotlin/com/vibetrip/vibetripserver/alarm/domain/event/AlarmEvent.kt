package com.vibetrip.vibetripserver.alarm.domain.event

import com.vibetrip.vibetripserver.alarm.domain.AlarmData

data class AlarmEvent(
    val memberKey: String,
    val alarmData: AlarmData,
)
