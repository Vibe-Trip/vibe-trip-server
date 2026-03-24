package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Title(
    val value: String,
) {
    companion object {
        private const val MAX_TITLE_LENGTH = 15
    }

    init {
        if (value.isBlank() || value.length > MAX_TITLE_LENGTH) {
            throw AppException(ErrorType.INVALID_ALBUM_TITLE)
        }
    }
}
