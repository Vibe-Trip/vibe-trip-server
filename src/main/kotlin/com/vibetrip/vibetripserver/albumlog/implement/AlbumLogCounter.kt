package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class AlbumLogCounter(
    private val albumLogRepository: AlbumLogRepository,
) {
    fun count(memberKey: String) = albumLogRepository.countByMemberKey(memberKey)
}
