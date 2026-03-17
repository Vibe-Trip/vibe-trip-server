package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.auth.domain.TokenType
import com.vibetrip.vibetripserver.common.exception.ErrorType
import io.jsonwebtoken.*
import io.jsonwebtoken.security.SecurityException
import org.springframework.stereotype.Component
import java.security.SignatureException
import javax.crypto.SecretKey

@Component
class JwtValidator(
    private val secretKey: SecretKey
) {
    companion object {
        private const val BEARER = "Bearer "
        private const val TOKEN_TYPE_CLAIM = "type"
    }

    fun getSubjectIfValidWithType(token: String, expectedType: TokenType): String {
        if (!isBearerToken(token)) {
            throw AppException(ErrorType.INVALID_TOKEN_METHOD)
        }

        val tokenBody = token.removePrefix(BEARER)

        return validate(tokenBody).payload
            .takeIf { it.get<String>(TOKEN_TYPE_CLAIM) == expectedType.name }
            ?.subject
            ?: throw AppException(ErrorType.INVALID_TOKEN_TYPE)
    }

    fun validate(token: String): Jws<Claims> =
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
        } catch (e: Exception) {
            throw when (e) {
                is MalformedJwtException -> AppException(ErrorType.MALFORMED_JWT)
                is UnsupportedJwtException -> AppException(ErrorType.UNSUPPORTED_JWT)
                is ExpiredJwtException -> AppException(ErrorType.EXPIRED_JWT)
                is SecurityException, is SignatureException -> AppException(ErrorType.INVALID_SIGNATURE)
                is IllegalArgumentException -> AppException(ErrorType.INVALID_JWT)
                else -> AppException(ErrorType.SERVER_ERROR)
            }
        }

    private fun isBearerToken(token: String) =
        token.startsWith(BEARER)

    private inline fun <reified T> Claims.get(claimName: String): T? {
        return this.get(claimName, T::class.java)
    }
}