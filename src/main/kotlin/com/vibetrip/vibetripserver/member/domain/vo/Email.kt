package com.vibetrip.vibetripserver.member.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Email(
    val value: String,
) {
    companion object {
        private val EMAIL_PATTERN =
            "^(?![.])(?!.*[.]{2})[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*\\.[A-Za-z]{2,}$"
                .toRegex()
    }

    init {
        if (value.isBlank() || !EMAIL_PATTERN.matches(value)) {
            throw AppException(ErrorType.INVALID_EMAIL)
        }
    }
}
