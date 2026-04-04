package com.vibetrip.vibetripserver.alarm.dataaccess.repository

import com.vibetrip.vibetripserver.alarm.dataaccess.entity.AlarmEntity
import com.vibetrip.vibetripserver.alarm.dataaccess.entity.QAlarmEntity.alarmEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import java.time.LocalDateTime

class CustomAlarmRepositoryImpl :
    QuerydslRepositorySupport(AlarmEntity::class),
    CustomAlarmRepository {
    override fun findByMemberKey(memberKey: String) =
        selectFrom(alarmEntity)
            .where(
                alarmEntity.memberKey.eq(memberKey),
                alarmEntity.status.eq(EntityStatus.ACTIVE),
            ).fetch()

    override fun delete(
        memberKey: String,
        alarmId: Long,
    ) {
        flush()

        update(alarmEntity)
            .set(alarmEntity.status, EntityStatus.DELETED)
            .set(alarmEntity.deletedAt, LocalDateTime.now())
            .where(
                alarmEntity.memberKey.eq(memberKey),
                alarmEntity.id.eq(alarmId),
            ).execute()

        clear()
    }
}
