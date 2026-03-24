package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.QAlbumLogEntity.albumLogEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport

class CustomAlbumLogRepositoryImpl :
    QuerydslRepositorySupport(AlbumLogEntity::class),
    CustomAlbumLogRepository {
    override fun find(id: Long) =
        selectFrom(albumLogEntity)
            .where(
                albumLogEntity.id.eq(id),
                albumLogEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne()

    override fun findByAlbumId(
        albumId: Long,
        cursorable: Cursorable<Long>,
    ): Slice<AlbumLogEntity> {
        val content =
            selectFrom(albumLogEntity)
                .where(
                    ltCursor(cursorable.cursor),
                    albumLogEntity.albumId.eq(albumId),
                    albumLogEntity.status.eq(EntityStatus.ACTIVE),
                ).orderBy(albumLogEntity.id.desc())
                .limit(cursorable.limit + 1L)
                .fetch()

        return Slice(content, cursorable, hasNext(cursorable, content))
    }

    private fun ltCursor(cursor: Long?) = cursor?.let { albumLogEntity.id.lt(it) }
}
