package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.domain.AppleKeys
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class AppleClient(
    private val restClient: RestClient,
) {
    companion object {
        private const val APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys"
    }

    @Cacheable(cacheNames = ["appleKeys"])
    fun fetchAppleKeys(): AppleKeys {
        logger.info { "[Network Request] Apple 서버에서 공개키 목록을 조회합니다." }

        return restClient
            .get().uri(APPLE_JWKS_URL)
            .retrieve()
            .body<AppleKeys>()
            ?: throw AppException(ErrorType.FAILED_REQUEST_APPLE_KEYS)
    }
}