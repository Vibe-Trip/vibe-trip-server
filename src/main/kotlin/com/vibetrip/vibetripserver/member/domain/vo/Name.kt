package com.vibetrip.vibetripserver.member.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Name(
    val value: String,
) {
    companion object {
        private const val MAX_NAME_LENGTH = 20
    }

    init {
        if (value.length > MAX_NAME_LENGTH) {
            throw AppException(ErrorType.INVALID_NAME_LENGTH)
        }
    }
}
