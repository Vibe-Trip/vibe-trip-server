package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Comment(val value: String) {
    init {
        if(value.length > 500) {
            throw AppException(ErrorType.INVALID_ALBUM_COMMENT)
        }
    }
}