package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.implement.ai.ImageAnalyzer
import com.vibetrip.vibetripserver.album.implement.ai.MusicGenerator
import com.vibetrip.vibetripserver.album.implement.ai.TitleGenerator
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AiProcessor(
    private val albumManager: AlbumManager,
    private val albumMusicManager: AlbumMusicManager,
    private val imageAnalyzer: ImageAnalyzer,
    private val titleGenerator: TitleGenerator,
    private val musicGenerator: MusicGenerator,
) {
    fun analyzeImage(gcsUri: String): String = imageAnalyzer.analyze(gcsUri)

    @Async
    fun generateTitle(
        albumId: Long,
        newAlbum: NewAlbum,
        imageKeywords: String,
    ) {
        titleGenerator.generateTitle(
            region = newAlbum.region.value,
            comment = newAlbum.comment.value,
            genre = newAlbum.genre,
            imageKeywords = imageKeywords,
        ).also { title -> albumManager.updateTitle(albumId, title) }
    }

    @Async
    fun generateMusic(
        albumId: Long,
        newAlbum: NewAlbum,
        imageKeywords: String,
    ) {
        runCatching {
            musicGenerator.generateMusic(
                region = newAlbum.region.value,
                comment = newAlbum.comment.value,
                genre = newAlbum.genre,
                vocalGender = newAlbum.vocalOption.vocalGender,
                withLyrics = newAlbum.vocalOption.withLyrics,
                imageKeywords = imageKeywords,
            )
        }.onSuccess { music ->
            albumMusicManager.save(albumId, newAlbum, music)
        }.onFailure { e ->
            logger.error { "[음악 생성 실패] albumId=$albumId | ${e.message}" }
            // TODO: FCM 실패 알림 발송 (#9)
        }
    }
}