package com.vibetrip.vibetripserver.alarm.domain

import com.vibetrip.vibetripserver.common.exception.ErrorMessage
import com.vibetrip.vibetripserver.common.exception.ErrorType

data class FcmAlarm<T>(
    val type: AlarmType,
    val data: T?,
    val error: ErrorMessage?,
) {
    companion object {
        fun <S> success(
            type: AlarmType,
            data: S? = null,
        ): FcmAlarm<S> = FcmAlarm(type, data, null)

        fun error(
            type: AlarmType,
            error: ErrorType,
            errorData: Any? = null,
        ): FcmAlarm<Unit> = FcmAlarm(type, null, ErrorMessage(error, errorData))
    }
}
