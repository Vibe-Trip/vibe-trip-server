package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.QAlbumLogEntity.albumLogEntity
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

    override fun findByAlbumId(
        albumId: Long,
        count: Long,
    ): List<AlbumLogImageEntity> =
        selectFrom(albumLogImageEntity)
            .join(albumLogEntity)
            .on(albumLogImageEntity.albumLogId.eq(albumLogEntity.id))
            .where(
                albumLogEntity.albumId.eq(albumId),
                albumLogEntity.status.eq(EntityStatus.ACTIVE),
                albumLogImageEntity.status.eq(EntityStatus.ACTIVE),
            ).limit(count)
            .fetch()

    override fun countByAlbumId(albumId: Long): Long =
        select(albumLogImageEntity.count())
            .from(albumLogImageEntity)
            .join(albumLogEntity)
            .on(albumLogImageEntity.albumLogId.eq(albumLogEntity.id))
            .where(
                albumLogEntity.albumId.eq(albumId),
                albumLogEntity.status.eq(EntityStatus.ACTIVE),
                albumLogImageEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne() ?: 0L

    override fun deleteByIds(ids: List<Long>) {
        if (ids.isEmpty()) return

        flush()

        update(albumLogImageEntity)
            .set(albumLogImageEntity.status, EntityStatus.DELETED)
            .set(albumLogImageEntity.deletedAt, LocalDateTime.now())
            .where(albumLogImageEntity.id.`in`(ids))
            .execute()

        clear()
    }

    override fun deleteByAlbumLogId(albumLogId: Long) {
        flush()

        update(albumLogImageEntity)
            .set(albumLogImageEntity.status, EntityStatus.DELETED)
            .set(albumLogImageEntity.deletedAt, LocalDateTime.now())
            .where(albumLogImageEntity.albumLogId.eq(albumLogId))
            .execute()

        clear()
    }
}
