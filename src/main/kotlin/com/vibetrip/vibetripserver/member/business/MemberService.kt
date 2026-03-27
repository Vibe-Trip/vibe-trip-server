package com.vibetrip.vibetripserver.member.business

import com.vibetrip.vibetripserver.member.implement.MemberWithdrawer
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberWithdrawer: MemberWithdrawer,
) {
    fun withdraw(memberKey: String) = memberWithdrawer.withdraw(memberKey)
}
