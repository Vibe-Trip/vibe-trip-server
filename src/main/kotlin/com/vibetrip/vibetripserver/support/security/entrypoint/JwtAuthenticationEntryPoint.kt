package com.vibetrip.vibetripserver.support.security.entrypoint

import com.vibetrip.vibetripserver.support.security.handler.AuthenticationExceptionHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationEntryPoint(
    private val exceptionHandler: AuthenticationExceptionHandler
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        exceptionHandler.handle(request, response, authException)
    }
}