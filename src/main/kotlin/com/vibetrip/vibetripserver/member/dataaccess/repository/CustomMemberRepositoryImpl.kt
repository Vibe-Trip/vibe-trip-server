package com.vibetrip.vibetripserver.member.dataaccess.repository

import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.member.dataaccess.entity.MemberEntity
import com.vibetrip.vibetripserver.member.dataaccess.entity.QMemberEntity.memberEntity
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import java.time.LocalDateTime

class CustomMemberRepositoryImpl :
    QuerydslRepositorySupport(MemberEntity::class),
    CustomMemberRepository {
    override fun deleteByMemberKey(memberKey: String) {
        flush()

        update(memberEntity)
            .set(memberEntity.status, EntityStatus.DELETED)
            .set(memberEntity.deletedAt, LocalDateTime.now())
            .where(memberEntity.memberKey.eq(memberKey))
            .execute()

        clear()
    }
}
