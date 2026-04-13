package com.vibetrip.vibetripserver.support.security.filter

import com.vibetrip.vibetripserver.auth.domain.AuthMember
import com.vibetrip.vibetripserver.auth.domain.TokenType
import com.vibetrip.vibetripserver.auth.implement.JwtValidator
import com.vibetrip.vibetripserver.common.domain.Api.Companion.post
import com.vibetrip.vibetripserver.member.implement.MemberFinder
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtValidator: JwtValidator,
    private val memberFinder: MemberFinder,
    private val antPathMatcher: AntPathMatcher,
) : OncePerRequestFilter() {
    companion object {
        private val PERMIT_PATTERNS =
            listOf(
                post("/api/v1/auth/login/**"),
                post("/api/v1/auth/refresh"),
                post("/api/v1/albums/suno/callback"),
            )
    }

    override fun shouldNotFilter(request: HttpServletRequest) =
        PERMIT_PATTERNS.any {
            antPathMatcher.match(it.url, request.requestURI) && it.matchesMethod(request.method)
        }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        getTokenFromHeader(request)?.let {
            authenticate(it)
        }

        doFilter(request, response, filterChain)
    }

    private fun getTokenFromHeader(request: HttpServletRequest): String? = request.getHeader(HttpHeaders.AUTHORIZATION)

    private fun authenticate(token: String) {
        val subject =
            jwtValidator.getBearerTokenBody(token).let {
                jwtValidator.getSubjectIfValidWithType(it, TokenType.ACCESS)
            }

        memberFinder.find(subject).run {
            val authMember = AuthMember(this, emptyMap(), authorities)

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(authMember, null, authorities)
        }
    }
}
