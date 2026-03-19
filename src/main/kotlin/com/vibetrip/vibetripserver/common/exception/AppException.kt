package com.vibetrip.vibetripserver.common.exception

class AppException(
    val errorType: ErrorType
) : RuntimeException()