package com.vibetrip.vibetripserver.alarm.presentation.dto.response

import com.vibetrip.vibetripserver.alarm.dataaccess.entity.AlarmEntity
import com.vibetrip.vibetripserver.alarm.domain.AlarmType
import java.time.LocalDateTime

data class AlarmResponse(
    val title: String,
    val description: String,
    val alarmType: AlarmType,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(alarm: AlarmEntity) =
            AlarmResponse(
                title = alarm.title,
                description = alarm.description,
                alarmType = alarm.alarmType,
                createdAt = alarm.createdAt,
            )
    }
}
