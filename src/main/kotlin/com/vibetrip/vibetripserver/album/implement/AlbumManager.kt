package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMemberEntity
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumMemberRepository
import com.vibetrip.vibetripserver.album.dataaccess.repository.AlbumRepository
import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.vo.Title
import com.vibetrip.vibetripserver.album.implement.ai.ImageAnalyzer
import com.vibetrip.vibetripserver.album.implement.ai.MusicGenerator
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Component
@Transactional
class AlbumManager(
    private val albumRepository: AlbumRepository,
    private val albumMemberRepository: AlbumMemberRepository,
    private val imageAnalyzer: ImageAnalyzer,
    private val musicGenerator: MusicGenerator,
    private val albumMusicManager: AlbumMusicManager,
    private val deletionProcessors: List<AlbumDeletionProcessor>,
) {
    fun create(
        newAlbum: NewAlbum,
        coverImageUrl: String,
    ): Long =
        albumRepository
            .save(AlbumEntity.from(newAlbum, coverImageUrl))
            .also {
                albumMemberRepository.save(AlbumMemberEntity(memberKey = it.memberKey, albumId = it.id!!))
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

    fun findAlbum(albumId: Long): Album =
        albumRepository.find(albumId)?.toDomain()
            ?: throw AppException(ErrorType.NOT_FOUND_ALBUM)

    fun delete(albumId: Long) {
        deletionProcessors.forEach { it.process(albumId) }
        albumRepository.deleteByAlbumId(albumId)
    }

    @Async("musicGenerationExecutor")
    fun generateMusic(
        albumId: Long,
        newAlbum: NewAlbum,
        coverImage: MultipartFile,
    ) {
        try {
            val imageAnalysis =
                imageAnalyzer.analyze(
                    image = coverImage,
                    region = newAlbum.region.value,
                    comment = newAlbum.comment.value,
                    genre = newAlbum.genre.value,
                    vocalGender = newAlbum.vocalOption.vocalGender,
                )
            val musicGenerateResponse =
                musicGenerator.generate(
                    genre = newAlbum.genre.value,
                    vocalGender = newAlbum.vocalOption.vocalGender,
                    imageAnalysis = imageAnalysis,
                )

            updateTitle(albumId, imageAnalysis.title)
            albumMusicManager.save(albumId, newAlbum, musicGenerateResponse.data.taskId, AlbumMusic.empty())
            logger.info { "[음악 생성 완료] albumId=$albumId" }
        } catch (e: Exception) {
            logger.error { "[음악 생성 실패] albumId=$albumId | ${e.message}" }
        }
    }
}
