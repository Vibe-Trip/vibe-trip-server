package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.album.implement.ai.ImageAnalyzer
import com.vibetrip.vibetripserver.album.implement.ai.MusicGenerator
import com.vibetrip.vibetripserver.common.domain.ImageData
import com.vibetrip.vibetripserver.common.log.logger
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import com.vibetrip.vibetripserver.support.paging.Cursorable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AlbumService(
    private val albumManager: AlbumManager,
    private val albumMusicManager: AlbumMusicManager,
    private val googleImageUploader: GoogleImageUploader,
    private val imageAnalyzer: ImageAnalyzer,
    private val musicGenerator: MusicGenerator,
) {
    @Transactional
    fun createAlbum(
        newAlbum: NewAlbum,
        coverImage: MultipartFile,
    ): Long {
        val contentType = validateImageContentType(coverImage.contentType)
        val coverImageUrl =
            googleImageUploader.uploadImage(
                ImageData(
                    coverImage.inputStream,
                    contentType,
                    coverImage.originalFilename!!,
                ),
            )
        val albumId = albumManager.create(newAlbum, coverImageUrl)

        val imageKeywords = imageAnalyzer.analyze(coverImage)
        runCatching {
            musicGenerator.generateMusic(
                region = newAlbum.region.value,
                comment = newAlbum.comment.value,
                genre = newAlbum.genre.value,
                vocalGender = newAlbum.vocalOption.vocalGender,
                imageKeywords = imageKeywords,
            )
        }.onSuccess { music ->
            albumMusicManager.save(albumId, newAlbum, music)
        }.onFailure { e ->
            logger.error { "[음악 생성 실패] albumId=$albumId | ${e.message}" }
        }
        return albumId
    }

    fun getAlbumCount(memberKey: String) = albumManager.count(memberKey)

    fun findAlbums(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ) = albumManager.find(memberKey, cursorable)

    fun countAlbums(memberKey: String): Long = albumManager.count(memberKey)
}
