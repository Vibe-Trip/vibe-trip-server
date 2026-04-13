package com.vibetrip.vibetripserver.member.implement

import com.vibetrip.vibetripserver.member.dataaccess.entity.MemberEntity
import com.vibetrip.vibetripserver.member.dataaccess.repository.MemberRepository
import com.vibetrip.vibetripserver.member.domain.NewMember
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class MemberRegistrar(
    private val memberRepository: MemberRepository,
) {
    fun register(newMember: NewMember) = memberRepository.save(MemberEntity.from(newMember)).memberKey
}
