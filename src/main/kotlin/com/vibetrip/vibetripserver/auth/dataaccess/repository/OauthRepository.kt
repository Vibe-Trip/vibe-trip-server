package com.vibetrip.vibetripserver.auth.dataaccess.repository

import com.vibetrip.vibetripserver.auth.dataaccess.entity.OauthEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OauthRepository : JpaRepository<OauthEntity, Long>, CustomOauthRepository {
}