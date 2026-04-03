package com.vibetrip.vibetripserver.config.cloud

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class FCMConfig(
    @Value("\${firebase.credentials.location}")
    private val credentialsLocation: String,
) {
    @PostConstruct
    fun initialize() {
        if (FirebaseApp.getApps().isNotEmpty()) return

        val credentials =
            GoogleCredentials.fromStream(
                ClassPathResource(credentialsLocation).inputStream,
            )
        FirebaseApp.initializeApp(
            FirebaseOptions
                .builder()
                .setCredentials(credentials)
                .build(),
        )
    }
}
