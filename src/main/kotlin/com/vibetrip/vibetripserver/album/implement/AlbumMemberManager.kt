package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class AlbumMemberManager(
    private val albumMemberRepository: AlbumMemberRepository,
) {

    fun exists(albumId: Long, memberKey: String) =
        albumMemberRepository.existsByAlbumIdAndMemberKey(albumId, memberKey)

    fun validateMember(albumId: Long, memberKey: String) {
        if (!exists(albumId, memberKey)) {
            throw AppException(ErrorType.NOT_ALBUM_MEMBER)
        }
    }
}