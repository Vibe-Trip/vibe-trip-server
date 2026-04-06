package com.vibetrip.vibetripserver.alarm.domain

import com.vibetrip.vibetripserver.common.exception.ErrorMessage
import com.vibetrip.vibetripserver.common.exception.ErrorType

data class FcmAlarm<T>(
    val data: T?,
    val error: ErrorMessage?,
) {
    companion object {
        fun <S> success(data: S? = null): FcmAlarm<S> = FcmAlarm(data, null)

        fun error(
            error: ErrorType,
            errorData: Any? = null,
        ): FcmAlarm<Unit> = FcmAlarm(null, ErrorMessage(error, errorData))
    }
}
