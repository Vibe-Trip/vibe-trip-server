package com.vibetrip.vibetripserver.alarm.integration

import com.vibetrip.vibetripserver.alarm.domain.FcmAlarm
import com.vibetrip.vibetripserver.alarm.implement.FcmSender
import com.vibetrip.vibetripserver.support.integration.SpringTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired

class FcmSenderTest : SpringTest() {
    @Autowired
    lateinit var fcmSender: FcmSender

    @Test
    @EnabledIfEnvironmentVariable(named = "FCM_TOKEN", matches = ".+")
    fun `FCM 푸시 알림이 정상적으로 발송된다`() {
        fcmSender.sendFcm(
            fcmToken = System.getenv("FCM_TOKEN"),
            title = "푸시 알림 ",
            body = "FCM 푸시 알림 테스트입니다",
            data = FcmAlarm.success<Unit>(),
        )
    }
}
