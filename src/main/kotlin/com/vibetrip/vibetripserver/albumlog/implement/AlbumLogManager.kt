package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageRepository
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogRepository
import com.vibetrip.vibetripserver.albumlog.domain.AlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class AlbumLogManager(
    private val albumLogRepository: AlbumLogRepository,
    private val albumLogImageRepository: AlbumLogImageRepository,
) {
    fun register(newAlbumLog: NewAlbumLog) = albumLogRepository.save(AlbumLogEntity.from(newAlbumLog)).toDomain()

    fun find(
        albumId: Long,
        cursorable: Cursorable<Long>,
    ): Slice<AlbumLog> {
        val albumLogSlice = albumLogRepository.findByAlbumId(albumId, cursorable)

        val imageMap =
            albumLogImageRepository
                .findByAlbumLogIds(albumLogSlice.content.mapNotNull { it.id })
                .groupBy { it.albumLogId }

        return albumLogSlice.map {
            it.toDomain(imageMap[it.id].orEmpty().map(AlbumLogImageEntity::toDomain))
        }
    }

    fun count(memberKey: String) = albumLogRepository.countByMemberKey(memberKey)
    fun update(
        id: Long,
        description: String,
        removeImageIds: List<Long>,
    ) {
        albumLogRepository.find(id)?.update(description) ?: throw AppException(ErrorType.NOT_FOUND_DATA)

        albumLogImageRepository.deleteByIds(removeImageIds)
    }

    fun delete(id: Long) {
        albumLogRepository.delete(id)
        albumLogImageRepository.deleteByAlbumLogId(id)
    }
}
