package com.vibetrip.vibetripserver.auth.implement

import com.vibetrip.vibetripserver.auth.dataaccess.entity.OauthEntity
import com.vibetrip.vibetripserver.auth.dataaccess.repository.OauthRepository
import com.vibetrip.vibetripserver.auth.domain.OAuthMember
import com.vibetrip.vibetripserver.member.domain.MemberDevice
import com.vibetrip.vibetripserver.member.domain.NewMember
import com.vibetrip.vibetripserver.member.implement.MemberDeviceManager
import com.vibetrip.vibetripserver.member.implement.MemberRegistrar
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class OAuthRegistrar(
    private val oauthRepository: OauthRepository,
    private val memberRegistrar: MemberRegistrar,
    private val memberDeviceManager: MemberDeviceManager,
) {
    fun registerIfNewAndGetMemberKey(
        oauthMember: OAuthMember,
        fcmToken: String,
        deviceId: String,
    ) = oauthRepository
        .findByAccountAndProvider(oauthMember.account, oauthMember.provider)
        ?.memberKey
        ?.also { memberDeviceManager.saveOrUpdate(MemberDevice(deviceId, fcmToken, it)) }
        ?: register(oauthMember, fcmToken, deviceId)

    private fun register(
        oAuthMember: OAuthMember,
        fcmToken: String,
        deviceId: String,
    ): String {
        val memberKey = memberRegistrar.register(NewMember.from(oAuthMember))
        memberDeviceManager.save(MemberDevice(deviceId, fcmToken, memberKey))
        oauthRepository.save(OauthEntity.of(oAuthMember, memberKey))
        return memberKey
    }
}
