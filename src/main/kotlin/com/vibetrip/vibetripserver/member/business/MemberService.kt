package com.vibetrip.vibetripserver.member.business

import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.member.implement.MemberFinder
import org.springframework.stereotype.Service

@Service
class MemberService (
    private val memberFinder: MemberFinder,
) {
    fun getMember(memberKey: String): Member {

        return memberFinder.find(memberKey)
    }
}