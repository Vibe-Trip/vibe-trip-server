package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.member.implement.MemberWithdrawalPostProcessor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class AlbumLogWithdrawalPostProcessor(
    private val albumLogRepository: AlbumLogRepository,
) : MemberWithdrawalPostProcessor {
    override fun process(memberKey: String) {
        albumLogRepository.deleteByMemberKey(memberKey)
    }
}
