package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.QAlbumLogEntity.albumLogEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport

class CustomAlbumLogRepositoryImpl : QuerydslRepositorySupport(AlbumLogEntity::class), CustomAlbumLogRepository {

    override fun find(id: Long) =
        selectFrom(albumLogEntity)
            .where(
                albumLogEntity.id.eq(id),
                albumLogEntity.status.eq(EntityStatus.ACTIVE),
            )
            .fetchOne()
}