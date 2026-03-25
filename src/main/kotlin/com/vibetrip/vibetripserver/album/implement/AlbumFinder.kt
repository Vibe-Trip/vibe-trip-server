package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Component
class AlbumFinder(
    private val albumRepository: AlbumRepository,
) {
    fun findAllByMemberKey(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ): Slice<Album> {
        val albumSlice = albumRepository.findAllByMemberKey(memberKey, cursorable)

        return albumSlice.map { it.toDomain() }
    }

    fun countByMemberKey(memberKey: String): Long = albumRepository.countByMemberKey(memberKey)
}
