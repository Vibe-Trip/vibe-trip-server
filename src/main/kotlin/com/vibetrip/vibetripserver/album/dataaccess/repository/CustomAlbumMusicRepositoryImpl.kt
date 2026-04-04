package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.QAlbumMusicEntity.albumMusicEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import java.time.LocalDateTime

class CustomAlbumMusicRepositoryImpl :
    QuerydslRepositorySupport(AlbumMusicEntity::class),
    CustomAlbumMusicRepository {

    override fun findByTaskId(taskId: String): AlbumMusicEntity? =
        selectFrom(albumMusicEntity)
            .where(
                albumMusicEntity.taskId.eq(taskId),
                albumMusicEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne()

    override fun findByAlbumId(albumId: Long) =
        selectFrom(albumMusicEntity)
            .where(
                albumMusicEntity.albumId.eq(albumId),
                albumMusicEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne()

    override fun deleteByAlbumId(albumId: Long) {
        flush()
        update(albumMusicEntity)
            .set(albumMusicEntity.status, EntityStatus.DELETED)
            .set(albumMusicEntity.deletedAt, LocalDateTime.now())
            .where(albumMusicEntity.albumId.eq(albumId))
            .execute()

        clear()
    }
}
