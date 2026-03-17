package com.vibetrip.vibetripserver.support.response

import com.vibetrip.vibetripserver.common.exception.ErrorMessage
import com.vibetrip.vibetripserver.common.exception.ErrorType

data class ApiResponse<T>(
    val resultType: ResultType,
    val data: T?,
    val error: ErrorMessage?
) {

    companion object {
        fun <S> success(data: S? = null): ApiResponse<S> {
            return ApiResponse(ResultType.SUCCESS, data, null)
        }

        fun error(error: ErrorType, errorData: Any? = null): ApiResponse<Unit> {
            return ApiResponse(ResultType.ERROR, null, ErrorMessage(error, errorData))
        }
    }
}