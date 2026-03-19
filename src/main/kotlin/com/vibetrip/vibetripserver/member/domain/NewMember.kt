package com.vibetrip.vibetripserver.member.domain

import com.vibetrip.vibetripserver.auth.domain.OAuthMember
import com.vibetrip.vibetripserver.member.domain.vo.Email
import com.vibetrip.vibetripserver.member.domain.vo.Name
import com.vibetrip.vibetripserver.member.domain.vo.ProfileImageUrl

data class NewMember(
    val name: Name,
    val email: Email,
    val profileImageUrl: ProfileImageUrl,
    val signUpType: SignUpType,
) {
    companion object {
        fun of(
            name: String,
            email: String,
            profileImageUrl: String,
            signUpType: SignUpType,
        ) = NewMember(
            name = Name(name),
            email = Email(email),
            profileImageUrl = ProfileImageUrl(profileImageUrl),
            signUpType = signUpType,
        )

        fun from(oAuthMember: OAuthMember) = NewMember(
            name = oAuthMember.name,
            email = oAuthMember.email,
            profileImageUrl = oAuthMember.profileImageUrl,
            signUpType = SignUpType.OAUTH,
        )
    }

    val nameValue: String
        get() = name.value

    val emailValue: String
        get() = email.value

    val profileImageUrlValue: String
        get() = profileImageUrl.value
}
