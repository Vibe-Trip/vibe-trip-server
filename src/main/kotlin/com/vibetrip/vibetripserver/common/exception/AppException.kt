package com.vibetrip.vibetripserver.common.exception

class AppException(
    val errorType: ErrorType,
    val errorData: Any? = null,
) : RuntimeException()
