package com.vibetrip.vibetripserver.member.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Name(
    val value: String,
) {
    companion object {
        private const val NAME_MAX_LENGTH = 20
    }

    init {
        if (value.length > NAME_MAX_LENGTH) {
            throw AppException(ErrorType.INVALID_NAME_LENGTH)
        }
    }
}
