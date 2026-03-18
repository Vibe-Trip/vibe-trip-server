package com.vibetrip.vibetripserver.config.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties::class)
class CacheConfig(
    private val cacheProperties: CacheProperties,
) {

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()

        cacheProperties.configs.forEach { (cacheName, spec) ->
            cacheManager.registerCustomCache(
                cacheName,
                Caffeine.newBuilder()
                    .expireAfterWrite(spec.ttl)
                    .maximumSize(spec.maxSize)
                    .build()
            )
        }

        return cacheManager
    }
}
