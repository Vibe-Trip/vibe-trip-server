package com.vibetrip.vibetripserver.member.implement

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.member.dataaccess.repository.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberFinder(
    private val memberRepository: MemberRepository,
) {
    fun find(memberKey: String) =
        memberRepository.findByMemberKey(memberKey)?.toDomain() ?: throw AppException(ErrorType.NOT_FOUND_DATA)
}