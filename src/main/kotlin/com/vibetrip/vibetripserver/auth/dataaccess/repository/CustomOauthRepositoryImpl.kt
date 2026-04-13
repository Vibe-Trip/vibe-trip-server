package com.vibetrip.vibetripserver.auth.dataaccess.repository

import com.vibetrip.vibetripserver.auth.dataaccess.entity.OauthEntity
import com.vibetrip.vibetripserver.auth.dataaccess.entity.QOauthEntity.oauthEntity
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.common.enums.EntityStatus
import com.vibetrip.vibetripserver.member.dataaccess.entity.QMemberEntity.memberEntity
import com.vibetrip.vibetripserver.support.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class CustomOauthRepositoryImpl :
    QuerydslRepositorySupport(OauthEntity::class),
    CustomOauthRepository {
    override fun findByAccountAndProvider(
        account: String,
        provider: OAuthProvider,
    ): OauthEntity? =
        selectFrom(oauthEntity)
            .innerJoin(memberEntity)
            .on(oauthEntity.memberKey.eq(memberEntity.memberKey))
            .where(
                oauthEntity.account.eq(account),
                oauthEntity.provider.eq(provider),
                oauthEntity.status.eq(EntityStatus.ACTIVE),
            ).fetchOne()
}
