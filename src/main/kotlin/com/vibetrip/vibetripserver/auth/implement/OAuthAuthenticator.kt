package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.domain.OAuthProvider
import com.vibetrip.vibetripserver.auth.domain.OAuthMember

interface OAuthAuthenticator {
    val provider: OAuthProvider
    fun authenticate(token: String): OAuthMember
}