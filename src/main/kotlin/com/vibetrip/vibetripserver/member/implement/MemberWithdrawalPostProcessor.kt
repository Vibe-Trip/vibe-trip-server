package com.vibetrip.vibetripserver.member.implement

interface MemberWithdrawalPostProcessor {
    fun process(memberKey: String)
}
