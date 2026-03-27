package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.member.implement.MemberWithdrawalPostProcessor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class AlbumWithdrawalPostProcessor(
    private val albumRepository: AlbumRepository,
) : MemberWithdrawalPostProcessor {
    override fun process(memberKey: String) {
        albumRepository.deleteByMemberKey(memberKey)
    }
}
