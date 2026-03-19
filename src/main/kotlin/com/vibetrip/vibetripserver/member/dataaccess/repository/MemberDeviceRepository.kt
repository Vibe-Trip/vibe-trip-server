package com.vibetrip.vibetripserver.member.dataaccess.repository

import com.vibetrip.vibetripserver.member.dataaccess.entity.MemberDeviceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberDeviceRepository : JpaRepository<MemberDeviceEntity, Long> {
}