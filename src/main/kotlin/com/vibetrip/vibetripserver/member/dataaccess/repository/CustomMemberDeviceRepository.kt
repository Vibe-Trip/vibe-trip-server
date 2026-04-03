package com.vibetrip.vibetripserver.member.dataaccess.repository

import com.vibetrip.vibetripserver.member.dataaccess.entity.MemberDeviceEntity

interface CustomMemberDeviceRepository {
    fun findByMemberKey(memberKey: String): List<String>

    fun findByDeviceId(deviceId: String): MemberDeviceEntity?
}
