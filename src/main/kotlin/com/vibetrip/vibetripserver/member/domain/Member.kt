package com.vibetrip.vibetripserver.member.domain

import com.vibetrip.vibetripserver.member.domain.vo.Email
import com.vibetrip.vibetripserver.member.domain.vo.Name
import com.vibetrip.vibetripserver.member.domain.vo.ProfileImageUrl
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class Member(
    val memberKey: String,
    val name: Name,
    val email: Email,
    val profileImageUrl: ProfileImageUrl,
    val roles: Set<MemberRole>,
) {
    companion object {
        fun of(
            memberKey: String,
            name: String,
            email: String,
            profileImageUrl: String,
            roles: Set<MemberRole>,
        ) = Member(
            memberKey = memberKey,
            name = Name(name),
            email = Email(email),
            profileImageUrl = ProfileImageUrl(profileImageUrl),
            roles = roles,
        )
    }

    val authorities: List<SimpleGrantedAuthority>
        get() = roles.map { SimpleGrantedAuthority(it.name) }

    val nameValue: String
        get() = name.value

    val emailValue: String
        get() = email.value

    val profileImageUrlValue: String
        get() = profileImageUrl.value
}
