package com.vibetrip.vibetripserver.support.security.filter

import com.vibetrip.vibetripserver.auth.domain.AuthMember
import com.vibetrip.vibetripserver.auth.domain.TokenType
import com.vibetrip.vibetripserver.auth.implement.JwtValidator
import com.vibetrip.vibetripserver.member.implement.MemberFinder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtValidator: JwtValidator,
    private val memberFinder: MemberFinder,
) : OncePerRequestFilter() {
    companion object {
        private val PERMIT_URLS = listOf(
            "/api/v1/auth/"
        )
    }

    override fun shouldNotFilter(request: HttpServletRequest) =
        PERMIT_URLS.any { request.requestURI.startsWith(it) }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        getTokenFromHeader(request)?.let {
            authenticate(it)
        }

        doFilter(request, response, filterChain)
    }

    private fun getTokenFromHeader(request: HttpServletRequest): String? =
        request.getHeader(HttpHeaders.AUTHORIZATION)

    private fun authenticate(token: String) {
        val subject = jwtValidator.getBearerTokenBody(token).let {
            jwtValidator.getSubjectIfValidWithType(it, TokenType.ACCESS)
        }

        memberFinder.find(subject).run {
            val authMember = AuthMember(this, emptyMap(), authorities)

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(authMember, null, authorities)
        }
    }
}