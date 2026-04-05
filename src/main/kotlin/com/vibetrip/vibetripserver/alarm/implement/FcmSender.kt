package com.vibetrip.vibetripserver.alarm.implement

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.vibetrip.vibetripserver.alarm.domain.FcmAlarm
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

private const val PAYLOAD = "payload"

@Component
class FcmSender(
    private val objectMapper: ObjectMapper,
) {
    fun sendFcm(
        fcmToken: String,
        title: String = "",
        body: String = "",
        data: FcmAlarm<*>,
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
            messageBuilder.putData(PAYLOAD, objectMapper.writeValueAsString(data))
            FirebaseMessaging.getInstance().send(messageBuilder.build())
        }.onFailure { e ->
            logger.error { "[FCM 발송 실패] token=$fcmToken | ${e.message}" }
        }
    }
}
