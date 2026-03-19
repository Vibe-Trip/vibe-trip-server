package com.vibetrip.vibetripserver.config.cache

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "app.cache")
data class CacheProperties(
    val configs: Map<String, CacheConfigDetail> = emptyMap()
) {
    data class CacheConfigDetail(
        val ttl: Duration = Duration.ofSeconds(60),
        val maxSize: Long = 100,
    )
}
