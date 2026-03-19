package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.domain.NewOAuthLogin
import com.vibetrip.vibetripserver.auth.domain.OAuthMember
import com.vibetrip.vibetripserver.auth.domain.OAuthProvider

interface OAuthAuthenticator {
    val provider: OAuthProvider

    fun authenticate(newOAuthLogin: NewOAuthLogin): OAuthMember
}
