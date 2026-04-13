package com.vibetrip.vibetripserver.auth.dataaccess.repository

import com.vibetrip.vibetripserver.auth.dataaccess.entity.OauthEntity
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider

interface CustomOauthRepository {
    fun findByAccountAndProvider(
        account: String,
        provider: OAuthProvider,
    ): OauthEntity?
}
