package com.vibetrip.vibetripserver.config.auth

import com.vibetrip.vibetripserver.support.security.entrypoint.JwtAuthenticationEntryPoint
import com.vibetrip.vibetripserver.support.security.filter.AuthenticationExceptionTranslationFilter
import com.vibetrip.vibetripserver.support.security.filter.JwtAuthenticationFilter
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults
import javax.crypto.SecretKey
import kotlin.io.encoding.Base64

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val exceptionTranslationFilter: AuthenticationExceptionTranslationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
) {

    @Value($$"${jwt.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer = WebSecurityCustomizer { web ->
        web.ignoring().requestMatchers(
            "/h2-console/**", "/v3/swagger-ui/**",
            "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api-docs/**", "/favicon.ico",
            "/error"
        )
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val mvc = withDefaults()

        http {
            httpBasic { disable() }
            formLogin { disable() }
            logout { disable() }
            csrf { disable() }

            authorizeHttpRequests {
                authorize(mvc.matcher("/api/v1/auth/login/**"), permitAll)
                authorize(anyRequest, authenticated)
            }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            exceptionHandling {
                authenticationEntryPoint = jwtAuthenticationEntryPoint
            }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
            addFilterBefore<JwtAuthenticationFilter>(exceptionTranslationFilter)
        }

        return http.build()
    }

    @Bean
    fun key(): SecretKey {
        return Keys.hmacShaKeyFor(Base64.decode(secretKey))
    }
}