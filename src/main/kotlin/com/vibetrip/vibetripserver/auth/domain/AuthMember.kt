package com.vibetrip.vibetripserver.auth.domain

import com.vibetrip.vibetripserver.member.domain.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

data class AuthMember(
    val member: Member,
    val attributes: Map<String?, Any?>,
    val authorities: Collection<GrantedAuthority?>,
) : OAuth2User {

    override fun getAttributes(): Map<String?, Any?> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority?> = authorities

    override fun getName(): String = member.memberKey

}
