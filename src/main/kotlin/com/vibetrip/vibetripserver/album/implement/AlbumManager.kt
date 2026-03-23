package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

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
        albumRepository
            .findById(albumId)
            .orElseThrow { AppException(ErrorType.NOT_FOUND_ALBUM) }
            .updateTitle(title)
    }
}
