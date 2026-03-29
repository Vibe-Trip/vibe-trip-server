package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMemberEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.vo.Title
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class AlbumManager(
    private val albumRepository: AlbumRepository,
    private val albumMemberRepository: AlbumMemberRepository,
) {
    fun create(
        newAlbum: NewAlbum,
        coverImageUrl: String,
    ): Long =
        albumRepository
            .save(AlbumEntity.from(newAlbum, coverImageUrl))
            .also { album ->
                albumMemberRepository.save(AlbumMemberEntity(memberKey = newAlbum.memberKey, albumId = album.id!!))
            }.id!!

    fun updateTitle(
        albumId: Long,
        title: String,
    ) {
        val validatedTitle = Title(title)
        albumRepository
            .findById(albumId)
            .orElseThrow { AppException(ErrorType.NOT_FOUND_ALBUM) }
            .updateTitle(validatedTitle.value)
    }

    fun find(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ): Slice<Album> {
        val albumSlice = albumRepository.findAllByMemberKey(memberKey, cursorable)

        return albumSlice.map { it.toDomain() }
    }

    fun count(memberKey: String): Long = albumRepository.countByMemberKey(memberKey)
}
