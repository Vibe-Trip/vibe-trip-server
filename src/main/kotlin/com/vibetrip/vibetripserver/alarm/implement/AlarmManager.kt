package com.vibetrip.vibetripserver.alarm.implement

import com.vibetrip.vibetripserver.alarm.dataaccess.entity.AlarmEntity
import com.vibetrip.vibetripserver.alarm.dataaccess.repository.AlarmRepository
import com.vibetrip.vibetripserver.alarm.domain.AlarmType
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.notification.FcmSender
import com.vibetrip.vibetripserver.member.implement.MemberDeviceManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class AlarmManager(
    private val alarmRepository: AlarmRepository,
    private val memberDeviceManager: MemberDeviceManager,
    private val fcmSender: FcmSender,
) {
    fun sendCreating(memberKey: String) {
        val title = "앨범을 생성하는 중입니다"
        val body = "나만의 음악이 곧 탄생합니다. 완료되면 바로 알려드릴게요"
        save(memberKey, title, body, AlarmType.CREATING)
        sendFcm(memberKey, title, body)
    }

    fun sendCompleted(
        memberKey: String,
        albumId: Long,
        albumTitle: String,
    ) {
        val title = "앨범 생성 완료!"
        val body = "세상에 하나뿐인 '$albumTitle'이 완성되었습니다. 지금 바로 완성된 음악을 감상해보세요"
        save(memberKey, title, body, AlarmType.COMPLETED, albumId)
        sendFcm(memberKey, title, body, mapOf("albumId" to albumId.toString()))
    }

    fun sendFailed(
        memberKey: String,
        errorType: ErrorType,
    ) {
        val title = "앨범 생성에 실패했습니다"
        val body = "${errorType.message}으로 생성이 실패했습니다. 앨범 만들기를 다시 시도해 볼까요?"
        save(memberKey, title, body, AlarmType.FAILED)
        sendFcm(
            memberKey, title, body,
            mapOf(
                "errorCode" to errorType.errorCode.name,
                "errorMessage" to errorType.message,
            ),
        )
    }

    @Transactional(readOnly = true)
    fun findAll(memberKey: String): List<AlarmEntity> =
        alarmRepository.findByMemberKeyAndStatus(memberKey, EntityStatus.ACTIVE)

    fun delete(alarmId: Long) {
        val alarm = alarmRepository.findByIdOrNull(alarmId)
            ?: throw AppException(ErrorType.NOT_FOUND_DATA)
        alarm.delete()
    }

    private fun save(
        memberKey: String,
        title: String,
        body: String,
        alarmType: AlarmType,
        albumId: Long? = null,
    ) {
        alarmRepository.save(
            AlarmEntity(
                title = title,
                description = body,
                memberKey = memberKey,
                alarmType = alarmType,
                albumId = albumId,
            ),
        )
    }

    private fun sendFcm(
        memberKey: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap(),
    ) {
        memberDeviceManager.findFcmToken(memberKey).forEach {
            fcmSender.sendFcm(fcmToken = it, title = title, body = body, data = data)
        }
    }
}
