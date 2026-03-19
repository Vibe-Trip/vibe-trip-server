package com.vibetrip.vibetripserver.auth.dataaccess.repository

import com.vibetrip.vibetripserver.auth.dataaccess.entity.QRefreshTokenEntity.refreshTokenEntity
import com.vibetrip.vibetripserver.auth.dataaccess.entity.RefreshTokenEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport

class CustomRefreshTokenRepositoryImpl
    : QuerydslRepositorySupport(RefreshTokenEntity::class), CustomRefreshTokenRepository {

    override fun findByMemberKey(memberKey: String): RefreshTokenEntity? =
        selectFrom(refreshTokenEntity)
            .where(
                refreshTokenEntity.memberKey.eq(memberKey),
                refreshTokenEntity.status.eq(EntityStatus.ACTIVE),
            )
            .fetchOne()

    override fun find(id: Long): RefreshTokenEntity? =
        selectFrom(refreshTokenEntity)
            .where(
                refreshTokenEntity.id.eq(id),
                refreshTokenEntity.status.eq(EntityStatus.ACTIVE),
            )
            .fetchOne()
}