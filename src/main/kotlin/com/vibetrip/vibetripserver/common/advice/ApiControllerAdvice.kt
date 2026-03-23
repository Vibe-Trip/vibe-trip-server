package com.vibetrip.vibetripserver.common.advice

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import com.vibetrip.vibetripserver.support.response.ApiResponse
import org.springframework.boot.logging.LogLevel.ERROR
import org.springframework.boot.logging.LogLevel.WARN
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {
    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException): ResponseEntity<ApiResponse<Unit>> {
        logAppException(e)
        return ResponseEntity(ApiResponse.error(e.errorType, e.errorData), e.errorType.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        logException(e)
        return ResponseEntity(ApiResponse.error(ErrorType.SERVER_ERROR, e), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun logAppException(e: AppException) {
        val stackTrace = e.stackTrace.first()
        val status = e.errorType.status.value()
        val errorCode = e.errorType.errorCode
        val logMessage =
            "[AppException]: class=${stackTrace.className} | method=${stackTrace.methodName} | " +
                "line=${stackTrace.lineNumber} | status=$status | errorCode=$errorCode | " +
                "message=${e.message} | data=${e.cause}"

        when (e.errorType.logLevel) {
            ERROR -> logger.error { logMessage }
            WARN -> logger.warn { logMessage }
            else -> logger.info { logMessage }
        }
    }

    private fun logException(e: Exception) {
        val stackTrace = e.stackTrace.first()
        logger.error {
            "[Exception]: class=${stackTrace.className} | method=${stackTrace.methodName} | line=${stackTrace.lineNumber} | message=${e.message}"
        }
    }
}
