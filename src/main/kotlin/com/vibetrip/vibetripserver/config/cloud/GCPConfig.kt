package com.vibetrip.vibetripserver.config.cloud

import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GCPConfig {

    @Bean
    fun storage(): Storage = StorageOptions.getDefaultInstance().service
}