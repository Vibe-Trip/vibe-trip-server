package com.vibetrip.vibetripserver.album.implement


import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class AlbumCounter (
    private val albumRepository: AlbumRepository,
) {
    fun count(memberKey: String) = albumRepository.countByMemberKey(memberKey)
}