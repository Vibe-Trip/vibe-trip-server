package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.alarm.domain.AlarmData
import com.vibetrip.vibetripserver.alarm.implement.AlarmManager
import com.vibetrip.vibetripserver.album.domain.MusicCompletedEvent
import com.vibetrip.vibetripserver.album.domain.MusicCreatingEvent
import com.vibetrip.vibetripserver.album.domain.MusicGenerationFailedEvent
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MusicAlarmEventListener(
    private val alarmManager: AlarmManager,
) {
    @Async
    @EventListener
    fun handleCreating(event: MusicCreatingEvent) {
        alarmManager.send(event.memberKey, AlarmData.Creating(event.albumId))
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    fun handleFailed(event: MusicGenerationFailedEvent) {
        alarmManager.send(event.memberKey, AlarmData.Failed(event.albumId, ErrorType.SERVER_ERROR))
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleCompleted(event: MusicCompletedEvent) {
        alarmManager.send(event.memberKey, AlarmData.Completed(event.albumId, event.taskId, event.title))
    }
}
