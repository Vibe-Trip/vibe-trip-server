package com.vibetrip.vibetripserver.config.api

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig {
    companion object {
        private const val API_CONNECT_TIMEOUT_SECONDS = 5L
        private const val API_READ_TIMEOUT_SECONDS = 3L

        private const val AI_CONNECT_TIMEOUT_SECONDS = 5L
        private const val AI_READ_TIMEOUT_SECONDS = 20L
    }

    @Bean
    fun restClient(): RestClient =
        RestClient
            .builder()
            .requestFactory(createFactory())
            .defaultStatusHandler(
                { status -> status.isError },
                { request, response ->
                    throw AppException(ErrorType.SERVER_ERROR, response)
                },
            ).build()

    @Bean("aiRestClient")
    fun aiRestClient(): RestClient =
        RestClient
            .builder()
            .requestFactory(
                SimpleClientHttpRequestFactory().apply {
                    setConnectTimeout(Duration.ofSeconds(AI_CONNECT_TIMEOUT_SECONDS))
                    setReadTimeout(Duration.ofSeconds(AI_READ_TIMEOUT_SECONDS))
                },
            ).defaultStatusHandler(
                { status -> status.isError },
                { request, response ->
                    logger.error { response.body }
                },
            ).build()

    private fun createFactory() =
        SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(Duration.ofSeconds(API_CONNECT_TIMEOUT_SECONDS))
            setReadTimeout(Duration.ofSeconds(API_READ_TIMEOUT_SECONDS))
        }
}
