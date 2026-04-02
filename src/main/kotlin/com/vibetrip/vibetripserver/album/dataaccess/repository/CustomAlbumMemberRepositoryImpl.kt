package com.vibetrip.vibetripserver.album.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMemberEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.QAlbumMemberEntity.albumMemberEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import java.time.LocalDateTime

class CustomAlbumMemberRepositoryImpl :
    QuerydslRepositorySupport(AlbumMemberEntity::class),
    CustomAlbumMemberRepository {
    override fun existsByAlbumIdAndMemberKey(
        albumId: Long,
        memberKey: String,
    ) = selectOne()
        .from(albumMemberEntity)
        .where(
            albumMemberEntity.albumId.eq(albumId),
            albumMemberEntity.memberKey.eq(memberKey),
            albumMemberEntity.status.eq(EntityStatus.ACTIVE),
        ).fetchFirst() != null

    override fun deleteByAlbumId(albumId: Long) {
        flush()

        update(albumMemberEntity)
            .set(albumMemberEntity.status, EntityStatus.DELETED)
            .set(albumMemberEntity.deletedAt, LocalDateTime.now())
            .where(albumMemberEntity.albumId.eq(albumId))
            .execute()
        clear()
    }
}
