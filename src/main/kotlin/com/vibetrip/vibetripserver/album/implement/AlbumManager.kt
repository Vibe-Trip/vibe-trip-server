package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.vo.Title
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class AlbumManager(
    private val albumRepository: AlbumRepository,
) {
    fun create(
        newAlbum: NewAlbum,
        coverImageUrl: String,
    ): Long = albumRepository.save(AlbumEntity.from(newAlbum, coverImageUrl)).id!!

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
}
