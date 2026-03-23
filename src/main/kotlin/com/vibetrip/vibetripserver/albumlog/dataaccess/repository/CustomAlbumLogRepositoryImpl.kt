package com.vibetrip.vibetripserver.albumlog.dataaccess.repository

import com.vibetrip.vibetripserver.album.dataaccess.entity.QAlbumEntity.albumEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.QAlbumLogEntity.albumLogEntity
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class CustomAlbumLogRepositoryImpl :
    QuerydslRepositorySupport(AlbumLogEntity::class),
    CustomAlbumLogRepository {
    override fun countByMemberKey(memberKey: String): Long =
        select(albumLogEntity.count())
            .from(albumLogEntity)
            .join(albumEntity)
            .on(albumLogEntity.albumId.eq(albumEntity.id))
            .where(
                albumEntity.memberKey.eq(memberKey),
                albumEntity.status.eq(EntityStatus.ACTIVE),
                albumLogEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne() ?: 0L
}
