package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.QAlbumEntity.albumEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.QAlbumMemberEntity.albumMemberEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import java.time.LocalDateTime

class CustomAlbumRepositoryImpl :
    QuerydslRepositorySupport(AlbumEntity::class),
    CustomAlbumRepository {
    override fun find(albumId: Long) =
        selectFrom(albumEntity)
            .where(
                albumEntity.id.eq(albumId),
                albumEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne()

    override fun findAllByMemberKey(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ): Slice<AlbumEntity> {
        val albumIds =
            select(albumMemberEntity.albumId)
                .from(albumMemberEntity)
                .where(
                    albumMemberEntity.memberKey.eq(memberKey),
                    albumMemberEntity.status.eq(EntityStatus.ACTIVE),
                )

        val content =
            selectFrom(albumEntity)
                .where(
                    albumEntity.id.`in`(albumIds),
                    albumEntity.status.eq(EntityStatus.ACTIVE),
                    ltCursor(cursorable.cursor),
                ).orderBy(albumEntity.id.desc())
                .limit(cursorable.limit + 1L)
                .fetch()

        return Slice(content, cursorable, hasNext(cursorable, content))
    }

    override fun countByMemberKey(memberKey: String) =
        select(albumEntity.count())
            .from(albumEntity)
            .where(
                albumEntity.id.`in`(
                    select(albumMemberEntity.albumId)
                        .from(albumMemberEntity)
                        .where(
                            albumMemberEntity.memberKey.eq(memberKey),
                            albumMemberEntity.status.eq(EntityStatus.ACTIVE),
                        ),
                ),
                albumEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne() ?: 0L

    override fun deleteByMemberKey(memberKey: String) {
        flush()

        update(albumEntity)
            .set(albumEntity.status, EntityStatus.DELETED)
            .set(albumEntity.deletedAt, LocalDateTime.now())
            .where(albumEntity.memberKey.eq(memberKey))
            .execute()

        clear()
    }

    private fun ltCursor(cursor: Long?) = cursor?.let { albumEntity.id.lt(it) }
}
