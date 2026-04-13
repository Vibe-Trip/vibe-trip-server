package com.vibetrip.vibetripserver.member.implement

import com.vibetrip.vibetripserver.member.dataaccess.repository.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Transactional
@Component
class MemberWithdrawer(
    private val memberRepository: MemberRepository,
    private val postProcessor: List<MemberWithdrawalPostProcessor>,
) {
    fun withdraw(memberKey: String) {
        postProcessor.forEach { it.process(memberKey) }

        memberRepository.deleteByMemberKey(memberKey)
    }
}
