package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.QAlbumEntity.albumEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.QAlbumLogEntity.albumLogEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import java.time.LocalDateTime

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

    override fun delete(id: Long) {
        flush()

        update(albumLogEntity)
            .set(albumLogEntity.status, EntityStatus.DELETED)
            .set(albumLogEntity.deletedAt, LocalDateTime.now())
            .where(albumLogEntity.id.eq(id))
            .execute()

        clear()
    }

    override fun deleteByMemberKey(memberKey: String) {
        flush()

        update(albumLogEntity)
            .set(albumLogEntity.status, EntityStatus.DELETED)
            .set(albumLogEntity.deletedAt, LocalDateTime.now())
            .where(
                albumLogEntity.albumId.`in`(
                    select(albumEntity.id)
                        .from(albumEntity)
                        .where(albumEntity.memberKey.eq(memberKey)),
                ),
            ).execute()

        clear()
    }

    private fun ltCursor(cursor: Long?) = cursor?.let { albumLogEntity.id.lt(it) }
}
