package com.vibetrip.vibetripserver.member.dataaccess.entity

import com.vibetrip.vibetripserver.common.entity.BaseEntity
import com.vibetrip.vibetripserver.member.domain.MemberDevice
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "member")
@Entity
class MemberDeviceEntity(
    @Column(nullable = false)
    var deviceId: String,

    @Column(nullable = false)
    var fcmToken: String,

    @Column(nullable = false)
    var memberKey: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_device_id")
    var id: Long? = null,
) : BaseEntity() {
    companion object {
        fun from(memberDevice: MemberDevice) = MemberDeviceEntity(
            deviceId = memberDevice.deviceId,
            fcmToken = memberDevice.fcmToken,
            memberKey = memberDevice.memberKey,
        )
    }
}