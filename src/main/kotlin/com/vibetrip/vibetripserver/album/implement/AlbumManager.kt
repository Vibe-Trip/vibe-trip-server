package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMemberEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.album.domain.EditAlbum
import com.vibetrip.vibetripserver.album.domain.MusicCreatingEvent
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.vo.Title
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class AlbumManager(
    private val albumRepository: AlbumRepository,
    private val albumMemberRepository: AlbumMemberRepository,
    private val deletionProcessors: List<AlbumDeletionProcessor>,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun create(
        newAlbum: NewAlbum,
        coverImageUrl: String,
    ) = albumRepository
        .save(AlbumEntity.from(newAlbum, coverImageUrl))
        .also {
            albumMemberRepository.save(AlbumMemberEntity(memberKey = it.memberKey, albumId = it.id!!))
            eventPublisher.publishEvent(MusicCreatingEvent(albumId = it.id!!, memberKey = newAlbum.memberKey))
        }.id!!

    fun updateTitle(
        albumId: Long,
        title: String,
    ) {
        albumRepository.find(albumId)?.updateTitle(Title(title).title)
            ?: throw AppException(ErrorType.NOT_FOUND_ALBUM)
    }

    fun count(memberKey: String) = albumRepository.countByMemberKey(memberKey)

    fun find(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ): Slice<Album> = albumRepository.findAllByMemberKey(memberKey, cursorable).map(AlbumEntity::toDomain)

    fun update(
        albumId: Long,
        editAlbum: EditAlbum,
        coverImageUrl: String?,
    ) {
        albumRepository.find(albumId)?.updateAlbum(editAlbum, coverImageUrl)
            ?: throw AppException(ErrorType.NOT_FOUND_ALBUM)
    }

    fun findAlbum(albumId: Long): Album =
        albumRepository.find(albumId)?.toDomain()
            ?: throw AppException(ErrorType.NOT_FOUND_ALBUM)

    fun delete(albumId: Long) {
        deletionProcessors.forEach { it.process(albumId) }
        albumRepository.deleteByAlbumId(albumId)
    }
}
