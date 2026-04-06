package com.vibetrip.vibetripserver.member.dataaccess.repository

import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.member.dataaccess.entity.MemberDeviceEntity
import com.vibetrip.vibetripserver.member.dataaccess.entity.QMemberDeviceEntity.memberDeviceEntity
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport

class CustomMemberDeviceRepositoryImpl :
    QuerydslRepositorySupport(MemberDeviceEntity::class),
    CustomMemberDeviceRepository {
    override fun findByMemberKey(memberKey: String) =
        select(memberDeviceEntity.fcmToken)
            .from(memberDeviceEntity)
            .where(
                memberDeviceEntity.memberKey.eq(memberKey),
                memberDeviceEntity.status.eq(EntityStatus.ACTIVE),
            ).fetch()

    override fun findByDeviceIdAndMemberKey(
        deviceId: String,
        memberKey: String,
    ): MemberDeviceEntity? =
        selectFrom(memberDeviceEntity)
            .where(
                memberDeviceEntity.deviceId.eq(deviceId),
                memberDeviceEntity.memberKey.eq(memberKey),
                memberDeviceEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne()
}
