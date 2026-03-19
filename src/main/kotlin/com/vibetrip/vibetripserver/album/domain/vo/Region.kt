package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Region(val value: String) {
    init {
        if(value.isBlank() || value.length > 15){
            throw AppException(ErrorType.INVALID_ALBUM_REGION)
        }
    }
}