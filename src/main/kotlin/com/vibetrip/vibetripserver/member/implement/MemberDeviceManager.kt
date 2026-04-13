package com.vibetrip.vibetripserver.member.implement

import com.vibetrip.vibetripserver.member.dataaccess.entity.MemberDeviceEntity
import com.vibetrip.vibetripserver.member.dataaccess.repository.MemberDeviceRepository
import com.vibetrip.vibetripserver.member.domain.MemberDevice
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class MemberDeviceManager(
    private val memberDeviceRepository: MemberDeviceRepository,
) {
    fun save(memberDevice: MemberDevice) {
        memberDeviceRepository.save(MemberDeviceEntity.from(memberDevice))
    }

    @Transactional(readOnly = true)
    fun findFcmToken(memberKey: String) = memberDeviceRepository.findByMemberKey(memberKey)

    fun saveOrUpdate(memberDevice: MemberDevice) {
        memberDeviceRepository
            .findByDeviceIdAndMemberKey(memberDevice.deviceId, memberDevice.memberKey)
            ?.updateFcmToken(memberDevice.fcmToken)
            ?: memberDeviceRepository.save(MemberDeviceEntity.from(memberDevice))
    }
}
