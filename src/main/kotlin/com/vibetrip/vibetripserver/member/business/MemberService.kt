package com.vibetrip.vibetripserver.member.business

import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.member.implement.MemberFinder
import com.vibetrip.vibetripserver.member.implement.MemberWithdrawer
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberFinder: MemberFinder,
    private val memberWithdrawer: MemberWithdrawer,
) {
    fun getMember(memberKey: String): Member = memberFinder.find(memberKey)

    fun withdraw(memberKey: String) = memberWithdrawer.withdraw(memberKey)
}
