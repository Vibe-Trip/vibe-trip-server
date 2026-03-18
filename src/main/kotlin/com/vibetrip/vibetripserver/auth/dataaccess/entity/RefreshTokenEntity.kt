package com.vibetrip.vibetripserver.auth.dataaccess.entity

import com.vibetrip.vibetripserver.auth.domain.RefreshToken
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import jakarta.persistence.*

@Table(name = "refresh_token")
@Entity
class RefreshTokenEntity(
    @Column(nullable = false)
    var refreshToken: String,

    @Column(nullable = false)
    var memberKey: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    var id: Long? = null,
) : BaseEntity() {

    fun update(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    fun toDomain() = RefreshToken(
        id = id!!,
        refreshToken = refreshToken,
        memberKey = memberKey,
    )
}