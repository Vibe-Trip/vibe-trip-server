package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.dataaccess.entity.OauthEntity
import com.vibetrip.vibetripserver.auth.dataaccess.repository.OauthRepository
import com.vibetrip.vibetripserver.auth.domain.OAuthMember
import com.vibetrip.vibetripserver.member.domain.NewMember
import com.vibetrip.vibetripserver.member.implement.MemberRegistrar
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class OAuthRegistrar(
    private val oauthRepository: OauthRepository,
    private val memberRegistrar: MemberRegistrar,
) {
    fun registerIfNewAndGetMemberKey(oauthMember: OAuthMember) =
        oauthRepository.findByAccountAndProvider(oauthMember.account, oauthMember.provider)?.memberKey
            ?: register(oauthMember)

    private fun register(oAuthMember: OAuthMember) =
        memberRegistrar.register(NewMember.from(oAuthMember)).also {
            oauthRepository.save(OauthEntity.of(oAuthMember, it))
        }
}
