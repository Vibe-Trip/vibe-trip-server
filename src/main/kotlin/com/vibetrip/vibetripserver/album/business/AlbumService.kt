package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.SunoMusicData
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.common.domain.ImageData
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import com.vibetrip.vibetripserver.support.paging.Cursorable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AlbumService(
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
            albumManager.generateMusic(albumId, newAlbum, coverImage)
        }
    }

    fun getAlbumCount(memberKey: String) = albumManager.count(memberKey)

    fun findAlbums(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ) = albumManager.find(memberKey, cursorable)

    fun countAlbums(memberKey: String): Long = albumManager.count(memberKey)

    fun updateMusic(sunoMusicData: SunoMusicData) {
        albumMusicManager.update(sunoMusicData)
    }
}
