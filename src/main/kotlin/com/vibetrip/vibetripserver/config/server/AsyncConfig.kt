package com.vibetrip.vibetripserver.config.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfig {

    @Bean
    fun taskExecutor() = ThreadPoolTaskExecutor().apply {
        corePoolSize = 5
        maxPoolSize = 10
        queueCapacity = 25
        setThreadNamePrefix("album-log-image")
        initialize()
    }
}