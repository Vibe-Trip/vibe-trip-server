package com.vibetrip.vibetripserver.common.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.stereotype.Component

@Component
class FcmSender {
    fun sendFcm(
        fcmToken: String,
        title: String = "",
        body: String = "",
        data: Map<String, String> = emptyMap(),
    ) {
        runCatching {
            val messageBuilder =
                Message
                    .builder()
                    .setToken(fcmToken)
                    .setNotification(
                        Notification
                            .builder()
                            .setTitle(title)
                            .setBody(body)
                            .build(),
                    )
            data.forEach { (key, value) -> messageBuilder.putData(key, value) }
            FirebaseMessaging.getInstance().send(messageBuilder.build())
        }.onFailure { e ->
            logger.error { "[FCM 발송 실패] token=$fcmToken | ${e.message}" }
        }
    }
}
