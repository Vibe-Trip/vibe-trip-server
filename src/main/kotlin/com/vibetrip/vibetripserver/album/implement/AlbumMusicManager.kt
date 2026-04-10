package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.SunoMusicDataEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMusicRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.SunoMusicDataRepository
import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.MusicCompletedEvent
import com.vibetrip.vibetripserver.album.domain.MusicCreatingEvent
import com.vibetrip.vibetripserver.album.domain.MusicGenerationFailedEvent
import com.vibetrip.vibetripserver.album.domain.MusicInfo
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.SunoMusicData
import com.vibetrip.vibetripserver.album.implement.ai.ImageAnalyzer
import com.vibetrip.vibetripserver.album.implement.ai.MusicGenerator
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Component
@Transactional
class AlbumMusicManager(
    private val albumMusicRepository: AlbumMusicRepository,
    private val sunoMusicDataRepository: SunoMusicDataRepository,
    private val imageAnalyzer: ImageAnalyzer,
    private val musicGenerator: MusicGenerator,
    private val albumManager: AlbumManager,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Async("musicGenerationExecutor")
    fun generateMusic(
        albumId: Long,
        musicInfo: MusicInfo,
        memberKey: String,
        coverImage: MultipartFile,
        shouldUpdateTitle: Boolean = true,
    ) {
        try {
            eventPublisher.publishEvent(MusicCreatingEvent(albumId, memberKey))
            val imageAnalysis =
                imageAnalyzer.analyze(
                    image = coverImage,
                    region = musicInfo.regionValue,
                    comment = musicInfo.commentValue,
                    genre = musicInfo.genre,
                    vocalGender = musicInfo.vocalGender,
                )
            val taskId =
                musicGenerator
                    .generate(
                        genre = musicInfo.genre,
                        vocalGender = musicInfo.vocalGender,
                        imageAnalysis = imageAnalysis,
                    ).data.taskId

            if (shouldUpdateTitle) {
                albumManager.updateTitle(albumId, imageAnalysis.title)
            }

            val newAlbum =
                NewAlbum(
                    memberKey = memberKey,
                    region = musicInfo.region,
                    comment = musicInfo.comment,
                    travelDate = musicInfo.travelDate,
                    vocalGender = musicInfo.vocalGender,
                    genre = musicInfo.genre,
                )
            albumMusicRepository.save(AlbumMusicEntity.from(albumId, newAlbum, taskId, AlbumMusic.empty()))
            logger.info { "[음악 생성 요청] albumId=$albumId" }
        } catch (e: Exception) {
            eventPublisher.publishEvent(MusicGenerationFailedEvent(albumId, memberKey))
            logger.error { "[음악 생성 실패] albumId=$albumId | ${e.message}" }
        }
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

    fun completeMusicGeneration(
        albumId: Long,
        taskId: String,
    ) {
        albumManager
            .findAlbum(albumId)
            .let { eventPublisher.publishEvent(MusicCompletedEvent(albumId, taskId, it.memberKey, it.title)) }
    }
}
