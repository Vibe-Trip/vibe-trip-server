package com.vibetrip.vibetripserver.alarm.implement

import com.vibetrip.vibetripserver.alarm.dataaccess.entity.AlarmEntity
import com.vibetrip.vibetripserver.alarm.dataaccess.repository.AlarmRepository
import com.vibetrip.vibetripserver.alarm.domain.AlarmData
import com.vibetrip.vibetripserver.member.implement.MemberDeviceManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class AlarmManager(
    private val alarmRepository: AlarmRepository,
    private val memberDeviceManager: MemberDeviceManager,
    private val fcmSender: FcmSender,
) {
    fun send(
        memberKey: String,
        alarmData: AlarmData,
    ) {
        alarmRepository.save(
            AlarmEntity(
                title = alarmData.title,
                description = alarmData.description,
                memberKey = memberKey,
                alarmType = alarmData.type,
                albumId = alarmData.albumId,
            ),
        )

        memberDeviceManager.findFcmToken(memberKey).forEach {
            fcmSender.sendFcm(
                fcmToken = it,
                title = alarmData.title,
                body = alarmData.description,
                data = alarmData.toFcmData(),
            )
        }
    }

    @Transactional(readOnly = true)
    fun findAll(memberKey: String) = alarmRepository.findByMemberKey(memberKey)

    fun delete(
        alarmId: Long,
        memberKey: String,
    ) = alarmRepository.delete(memberKey, alarmId)

    fun deleteCreatingAlarm(albumId: Long) = alarmRepository.deleteCreatingByAlbumId(albumId)
}
