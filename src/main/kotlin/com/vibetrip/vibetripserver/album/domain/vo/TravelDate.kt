package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import java.time.LocalDate

data class TravelDate(
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    init {
        if (startDate.isAfter(endDate)) {
            throw AppException(ErrorType.INVALID_ALBUM_TRAVEL_DATE)
        }
    }
}
