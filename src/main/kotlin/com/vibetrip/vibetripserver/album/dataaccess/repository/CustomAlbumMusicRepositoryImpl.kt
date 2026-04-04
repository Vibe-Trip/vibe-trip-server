package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.QAlbumMusicEntity.albumMusicEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport

class CustomAlbumMusicRepositoryImpl :
    QuerydslRepositorySupport(AlbumMusicEntity::class),
    CustomAlbumMusicRepository {
    override fun findByTaskId(taskId: String): AlbumMusicEntity? =
        selectFrom(albumMusicEntity)
            .where(
                albumMusicEntity.taskId.eq(taskId),
                albumMusicEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne()
}
