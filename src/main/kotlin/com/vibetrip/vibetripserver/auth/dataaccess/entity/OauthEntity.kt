package com.vibetrip.vibetripserver.auth.dataaccess.entity

import com.vibetrip.vibetripserver.auth.domain.OAuthMember
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "oauth")
@Entity
class OauthEntity(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var provider: OAuthProvider,

    @Column(nullable = false)
    var account: String,

    @Column(nullable = false)
    var memberKey: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_id")
    var id: Long? = null,
) : BaseEntity() {
    companion object {
        fun of(oAuthMember: OAuthMember, memberKey: String) =
            OauthEntity(
                provider = oAuthMember.provider,
                account = oAuthMember.account,
                memberKey = memberKey,
            )
    }
}