package com.vibetrip.vibetripserver.albumlog.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Description(
        val value: String
) {
    companion object {
        private const val DESCRIPTION_MAX_LENGTH = 500
    }

    init {
        if (value.length > DESCRIPTION_MAX_LENGTH) {
            throw AppException(ErrorType.INVALID_DESCRIPTION_LENGTH)
        }
    }
}