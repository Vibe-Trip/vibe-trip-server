package com.vibetrip.vibetripserver.member.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import java.net.URI

@JvmInline
value class ProfileImageUrl(
    val value: String,
) {
    init {
        if (value.isNotEmpty() && !isValidUrl(value)) {
            throw AppException(ErrorType.INVALID_IMAGE_URL)
        }
    }

    private fun isValidUrl(url: String) =
        runCatching { URI(url) }
            .map { it.scheme != null && it.host != null }
            .getOrDefault(false)
}
