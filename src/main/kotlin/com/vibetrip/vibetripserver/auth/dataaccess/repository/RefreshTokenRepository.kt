package com.vibetrip.vibetripserver.auth.dataaccess.repository

import com.vibetrip.vibetripserver.auth.dataaccess.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository :
    JpaRepository<RefreshTokenEntity, Long>,
    CustomRefreshTokenRepository
