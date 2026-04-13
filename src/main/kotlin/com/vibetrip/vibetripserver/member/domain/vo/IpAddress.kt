package com.vibetrip.vibetripserver.member.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class IpAddress(
    val value: String,
) {
    companion object {
        private val IPV4_PATTERN = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$".toRegex()
    }

    init {
        if (!value.matches(IPV4_PATTERN)) {
            throw AppException(ErrorType.INVALID_ACCESS_PATH)
        }
    }
}
