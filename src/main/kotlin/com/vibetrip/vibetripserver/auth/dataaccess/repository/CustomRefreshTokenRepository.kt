package com.vibetrip.vibetripserver.auth.dataaccess.repository

import com.vibetrip.vibetripserver.auth.dataaccess.entity.RefreshTokenEntity

interface CustomRefreshTokenRepository {

    fun findByMemberKey(memberKey: String): RefreshTokenEntity?

    fun find(id: Long): RefreshTokenEntity?
}