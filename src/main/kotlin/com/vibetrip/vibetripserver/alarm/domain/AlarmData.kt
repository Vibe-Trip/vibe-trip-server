package com.vibetrip.vibetripserver.alarm.domain

import com.vibetrip.vibetripserver.common.exception.ErrorType

sealed class AlarmData {
    abstract val type: AlarmType
    abstract val title: String
    abstract val description: String

    abstract val albumId: Long?

    abstract fun toFcmData(): FcmAlarm<*>

    data class Creating(
        override val albumId: Long,
        val taskId: String,
    ) : AlarmData() {
        override val type: AlarmType = AlarmType.CREATING
        override val title: String = type.title
        override val description: String = type.description

        override fun toFcmData(): FcmAlarm<CreatingPayload> = FcmAlarm.success(CreatingPayload(albumId, taskId))

        data class CreatingPayload(
            val albumId: Long,
            val taskId: String,
        )
    }

    data class Completed(
        override val albumId: Long,
        val albumTitle: String,
    ) : AlarmData() {
        override val type: AlarmType = AlarmType.COMPLETED
        override val title: String = type.title
        override val description: String = type.description.format(albumTitle)

        override fun toFcmData(): FcmAlarm<CompletedPayload> = FcmAlarm.success(CompletedPayload(albumId))

        data class CompletedPayload(
            val albumId: Long,
        )
    }

    data class Failed(
        override val albumId: Long,
        val errorType: ErrorType,
    ) : AlarmData() {
        override val type: AlarmType = AlarmType.FAILED
        override val title: String = type.title
        override val description: String = type.description.format(errorType.message)

        override fun toFcmData(): FcmAlarm<Unit> = FcmAlarm.error(errorType, FailedPayload(albumId))

        data class FailedPayload(
            val albumId: Long,
        )
    }
}
