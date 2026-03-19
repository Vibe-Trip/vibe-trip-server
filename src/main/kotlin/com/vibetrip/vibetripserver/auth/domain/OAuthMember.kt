package com.vibetrip.vibetripserver.auth.domain

import com.vibetrip.vibetripserver.member.domain.vo.Email
import com.vibetrip.vibetripserver.member.domain.vo.Name
import com.vibetrip.vibetripserver.member.domain.vo.ProfileImageUrl

data class OAuthMember(
    val account: String,
    val provider: OAuthProvider,
    val name: Name,
    val email: Email,
    val profileImageUrl: ProfileImageUrl,
) {
    companion object {
        fun of(
            account: String,
            provider: OAuthProvider,
            name: String,
            email: String,
            profileImageUrl: String,
        ) = OAuthMember(
            account = account,
            provider = provider,
            name = Name(name),
            email = Email(email),
            profileImageUrl = ProfileImageUrl(profileImageUrl),
        )
    }
}
