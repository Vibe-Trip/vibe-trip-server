package com.vibetrip.vibetripserver.member.implement

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.member.dataaccess.repository.MemberRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class MemberFinder(
    private val memberRepository: MemberRepository,
) {
    fun find(memberKey: String) =
        memberRepository.findByMemberKey(memberKey)?.toDomain() ?: throw AppException(ErrorType.NOT_FOUND_DATA)
}