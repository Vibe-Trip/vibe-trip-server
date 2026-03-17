package com.vibetrip.vibetripserver.auth.dataaccess.entity

import com.vibetrip.vibetripserver.auth.domain.RefreshToken
import com.vibetrip.vibetripserver.common.entity.BaseEntity
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "refresh_token")
@Entity
class RefreshTokenEntity(
    @Column(nullable = false)
    var refreshToken: String,

    @Column(nullable = false)
    var memberKey: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_id")
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