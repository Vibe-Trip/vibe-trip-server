package com.vibetrip.vibetripserver.support.security.handler

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import com.vibetrip.vibetripserver.common.util.getClientIp
import com.vibetrip.vibetripserver.support.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.nio.charset.StandardCharsets

private const val USER_AGENT = "User-Agent"

@Component
class AuthenticationExceptionHandler(
    private val objectMapper: ObjectMapper
) {

    fun handle(request: HttpServletRequest, response: HttpServletResponse, exception: Exception) {
        if (response.isCommitted) {
            return
        }

        val errorType = when (exception) {
            is AppException -> exception.errorType
            is AuthenticationException -> exception.toErrorType()
            else -> ErrorType.FAILED_AUTH
        }

        writeUnauthorizedResponse(request, response, ApiResponse.error(errorType))
    }

    private fun AuthenticationException.toErrorType() = when (this) {
        is AuthenticationCredentialsNotFoundException,
        is InsufficientAuthenticationException -> ErrorType.REQUIRED_AUTH

        else -> ErrorType.FAILED_AUTH
    }

    private fun writeUnauthorizedResponse(
        request: HttpServletRequest,
        response: HttpServletResponse,
        body: ApiResponse<Unit>
    ) {
        val errorCode = body.error?.errorCode
        val message = body.error?.message

        logger.warn {
            "[Authentication Exception]: IP=${getClientIp(request)} | Method=${request.method} | " +
                    "URI=${request.requestURI} | UserAgent=${request.getHeader("User-Agent")} | " +
                    "errorCode=$errorCode | message=$message"
        }

        response.apply {
            status = HttpStatus.UNAUTHORIZED.value()
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = StandardCharsets.UTF_8.name()
        }.writer.use {
            it.write(objectMapper.writeValueAsString(body))
        }
    }
}