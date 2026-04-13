package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

data class Title(
    val title: String,
) {
    companion object {
        private const val MAX_TITLE_LENGTH = 15
    }

    init {
        if (title.isBlank() || title.length > MAX_TITLE_LENGTH) {
            throw AppException(ErrorType.INVALID_ALBUM_TITLE)
        }
    }
}
