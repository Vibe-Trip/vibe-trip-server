package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.QAlbumLogImageEntity.albumLogImageEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import java.time.LocalDateTime

class CustomAlbumLogImageRepositoryImpl :
    QuerydslRepositorySupport(AlbumLogImageEntity::class),
    CustomAlbumLogImageRepository {
    override fun findByAlbumLogIds(albumLogIds: List<Long>): List<AlbumLogImageEntity> =
        selectFrom(albumLogImageEntity)
            .where(
                albumLogImageEntity.albumLogId.`in`(albumLogIds),
                albumLogImageEntity.status.eq(EntityStatus.ACTIVE),
            ).fetch()

    override fun deleteByIds(ids: List<Long>) {
        update(albumLogImageEntity)
            .set(albumLogImageEntity.status, EntityStatus.DELETED)
            .set(albumLogImageEntity.deletedAt, LocalDateTime.now())
            .where(albumLogImageEntity.id.`in`(ids))
            .execute()
    }
}
