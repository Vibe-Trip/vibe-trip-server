package com.vibetrip.vibetripserver.common.exception

data class ErrorMessage(
    val errorCode: String,
    val message: String,
    val data: Any? = null,
) {
    constructor(errorType: ErrorType, data: Any?) : this(errorType.errorCode.name, errorType.message, data)
}
