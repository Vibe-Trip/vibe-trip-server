package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.domain.AppleKey
import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthMember
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import kotlin.io.encoding.Base64

@Component
class AppleAuthenticator(
    private val appleClient: AppleClient,
    private val objectMapper: ObjectMapper,
) : OAuthAuthenticator {
    companion object {
        private const val KID = "kid"
        private const val ISSUER = "https://appleid.apple.com"
        private const val AUDIENCE = "com.swyp.VibeTrip"
        private const val EMAIL_CLAIM = "email"

        private val base64UrlDecoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
    }

    override val provider = OAuthProvider.APPLE

    override fun authenticate(newOAuthLogin: NewOAuthLogin): OAuthMember {
        require(newOAuthLogin is NewOAuthLogin.Apple) {
            throw AppException(ErrorType.SERVER_ERROR)
        }

        val appleKeys = appleClient.fetchAppleKeys()

        val kid = extractKid(newOAuthLogin.authToken)
        val appleKey = appleKeys.keys.find { it.kid == kid } ?: throw AppException(ErrorType.INVALID_APPLE_KEY)
        val publicKey = generatePublicKey(appleKey)

        val claims = getClaims(publicKey, newOAuthLogin)

        return OAuthMember.of(
            account = claims.subject,
            provider = provider,
            name = newOAuthLogin.name ?: "",
            email = claims[EMAIL_CLAIM] as? String ?: "",
            profileImageUrl = "",
        )
    }

    private fun getClaims(
        publicKey: PublicKey,
        newOAuthLogin: NewOAuthLogin.Apple,
    ) = runCatching {
        Jwts
            .parser()
            .verifyWith(publicKey)
            .requireIssuer(ISSUER)
            .requireAudience(AUDIENCE)
            .build()
            .parseSignedClaims(newOAuthLogin.authToken)
            .payload
    }.getOrElse {
        throw when (it) {
            is ExpiredJwtException -> AppException(ErrorType.EXPIRED_JWT)
            else -> AppException(ErrorType.INVALID_APPLE_IDENTITY_TOKEN)
        }
    }

    private fun extractKid(token: String): String =
        runCatching {
            val header = base64UrlDecoder.decode(token.split(".").first())
            val headerMap = objectMapper.readValue(header, Map::class.java)

            headerMap[KID] as String
        }.getOrElse {
            throw AppException(ErrorType.INVALID_APPLE_IDENTITY_TOKEN)
        }

    private fun generatePublicKey(appleKey: AppleKey): PublicKey {
        val nBytes = base64UrlDecoder.decode(appleKey.n)
        val eBytes = base64UrlDecoder.decode(appleKey.e)

        val n = BigInteger(1, nBytes)
        val e = BigInteger(1, eBytes)

        val publicKeySpec = RSAPublicKeySpec(n, e)
        return KeyFactory.getInstance(appleKey.kty).generatePublic(publicKeySpec)
    }
}
