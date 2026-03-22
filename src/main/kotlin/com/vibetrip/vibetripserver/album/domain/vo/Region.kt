package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Region(
    val value: String,
) {
    companion object {
        private const val REGION_MAX_LENGTH = 15
    }

    init {
        if (value.length > REGION_MAX_LENGTH) {
            throw AppException(ErrorType.INVALID_ALBUM_REGION)
        }
    }
}