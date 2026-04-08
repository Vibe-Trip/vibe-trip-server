package com.vibetrip.vibetripserver.alarm.business

import com.vibetrip.vibetripserver.alarm.domain.event.AlarmEvent
import com.vibetrip.vibetripserver.alarm.implement.AlarmManager
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AlarmEventListener(
    private val alarmManager: AlarmManager,
) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAlarmEvent(event: AlarmEvent) {
        alarmManager.send(event.memberKey, event.alarmData)
    }
}
