package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.album.domain.AlbumDetail
import com.vibetrip.vibetripserver.album.domain.EditAlbum
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.SunoMusicData
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.common.domain.ImageData
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AlbumService(
    private val albumMemberManager: AlbumMemberManager,
    private val albumManager: AlbumManager,
    private val googleImageUploader: GoogleImageUploader,
    private val albumMusicManager: AlbumMusicManager,
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

        return albumManager.create(newAlbum, coverImageUrl).also { albumId ->
            albumMusicManager.generateMusic(albumId, newAlbum.toMusicInfo(), newAlbum.memberKey, coverImage.bytes)
        }
    }

    fun getAlbumCount(memberKey: String) = albumManager.count(memberKey)

    fun findAlbums(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ): Slice<Album> = albumManager.find(memberKey, cursorable)

    fun findAlbum(
        albumId: Long,
        memberKey: String,
    ): AlbumDetail {
        albumMemberManager.validateMember(albumId, memberKey)
        val album = albumManager.findAlbum(albumId)
        val albumMusic = albumMusicManager.findMusic(albumId)
        return albumMusic?.let {
            AlbumDetail(
                album = album,
                musicUrl = it.musicUrl,
                genre = it.genre,
                withLyrics = it.withLyrics,
                vocalGender = it.vocalGender,
            )
        } ?: AlbumDetail(album)
    }

    fun countAlbums(memberKey: String): Long = albumManager.count(memberKey)

    @Retryable(
        retryFor = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 100),
    )
    @Transactional
    fun updateAlbum(
        albumId: Long,
        editAlbum: EditAlbum,
        coverImage: MultipartFile?,
        regenerateMusic: Boolean,
    ) {
        albumMemberManager.validateMember(albumId, editAlbum.memberKey)

        if (regenerateMusic) {
            albumMusicManager.delete(albumId)
            albumMusicManager.generateMusic(
                albumId = albumId,
                musicInfo = editAlbum.toMusicInfo(),
                coverImageBytes = coverImage?.bytes ?: throw AppException(ErrorType.IMAGE_NOT_EXISTS),
                memberKey = editAlbum.memberKey,
                shouldUpdateTitle = false,
            )
        }

        val coverImageUrl =
            coverImage?.let {
                googleImageUploader.uploadImage(
                    ImageData(
                        it.inputStream,
                        validateImageContentType(it.contentType),
                        it.originalFilename!!,
                    ),
                )
            }
        albumManager.update(albumId, editAlbum, coverImageUrl)
    }

    fun deleteAlbum(
        albumId: Long,
        memberKey: String,
    ) {
        albumMemberManager.validateMember(albumId, memberKey)
        albumManager.delete(albumId)
    }

    fun updateMusic(sunoMusicData: SunoMusicData) {
        albumMusicManager
            .update(sunoMusicData)
            .also { albumMusicManager.completeMusicGeneration(it, sunoMusicData.taskId) }
    }
}
