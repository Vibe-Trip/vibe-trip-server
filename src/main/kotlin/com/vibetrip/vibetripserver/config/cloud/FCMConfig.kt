package com.vibetrip.vibetripserver.config.cloud

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import kotlin.io.encoding.Base64

@Configuration
class FCMConfig(
    @Value($$"${firebase.key}")
    private val credentialsBase64: String,
) {
    @PostConstruct
    fun initialize() {
        if (FirebaseApp.getApps().isNotEmpty()) return

        val credentials =
            GoogleCredentials.fromStream(
                Base64.decode(credentialsBase64).inputStream(),
            )
        FirebaseApp.initializeApp(
            FirebaseOptions
                .builder()
                .setCredentials(credentials)
                .build(),
        )
    }
}
