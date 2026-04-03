package com.vibetrip.vibetripserver.alarm.dataaccess.repository

import com.vibetrip.vibetripserver.alarm.dataaccess.entity.AlarmEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import org.springframework.data.jpa.repository.JpaRepository

interface AlarmRepository : JpaRepository<AlarmEntity, Long> {
    fun findByMemberKeyAndStatus(memberKey: String, status: EntityStatus): List<AlarmEntity>
}
