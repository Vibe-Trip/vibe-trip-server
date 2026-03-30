package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

data class VocalOption(
    val withLyrics: Boolean,
    val vocalGender: VocalGender = VocalGender.N,
) {
    init {
        if (withLyrics && vocalGender == VocalGender.N) {
            throw AppException(ErrorType.INVALID_VOCAL_GENDER)
        }
    }
}
