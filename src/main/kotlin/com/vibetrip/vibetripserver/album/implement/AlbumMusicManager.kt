package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.SunoMusicDataEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.SunoMusicDataRepository
import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.SunoMusicData
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class AlbumMusicManager(
    private val albumMusicRepository: AlbumMusicRepository,
    private val sunoMusicDataRepository: SunoMusicDataRepository,
) {
    fun save(
        albumId: Long,
        newAlbum: NewAlbum,
        taskId: String,
        music: AlbumMusic,
    ) {
        albumMusicRepository.save(AlbumMusicEntity.from(albumId, newAlbum, taskId, music))
    }

    fun update(sunoMusicData: SunoMusicData): Long {
        sunoMusicDataRepository.save(SunoMusicDataEntity.from(sunoMusicData))
        val entity =
            albumMusicRepository.findByTaskId(sunoMusicData.taskId)
                ?: throw AppException(ErrorType.NOT_FOUND_DATA)
        entity.update(sunoMusicData.audioUrl, sunoMusicData.prompt)
        return entity.albumId
    }

    fun delete(albumId: Long) = albumMusicRepository.deleteByAlbumId(albumId)

    @Transactional(readOnly = true)
    fun findMusic(albumId: Long) = albumMusicRepository.findByAlbumId(albumId)
}
