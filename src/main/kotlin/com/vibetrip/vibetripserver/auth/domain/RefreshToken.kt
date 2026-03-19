package com.vibetrip.vibetripserver.auth.domain

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

data class RefreshToken(
    val id: Long,
    val refreshToken: String,
    val memberKey: String,
) {
    fun validateReuse(
        refreshToken: String,
        onReuse: RefreshToken.() -> Unit,
    ) {
        if (this.refreshToken != refreshToken) {
            this.onReuse()
            throw AppException(ErrorType.FAILED_AUTH)
        }
    }
}
