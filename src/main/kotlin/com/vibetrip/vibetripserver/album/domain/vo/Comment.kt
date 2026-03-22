package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Comment(
    val value: String,
) {
    companion object {
        private const val COMMENT_MAX_LENGTH = 500
    }

    init {
        if (value.length > COMMENT_MAX_LENGTH) {
            throw AppException(ErrorType.INVALID_ALBUM_COMMENT)
        }
    }
}