package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.Album
import com.vibetrip.vibetripserver.album.domain.AlbumDetail
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.SunoMusicData
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.common.domain.ImageData
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.paging.Slice
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
            albumManager.generateMusic(albumId, newAlbum, coverImage)
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
        val musicUrl = albumMusicManager.getMusicUrl(albumId)

        return AlbumDetail(album, musicUrl)
    }

    fun countAlbums(memberKey: String): Long = albumManager.count(memberKey)

    fun deleteAlbum(
        albumId: Long,
        memberKey: String,
    ) {
        albumMemberManager.validateMember(albumId, memberKey)
        albumManager.delete(albumId)
    }

    fun updateMusic(sunoMusicData: SunoMusicData) {
        albumMusicManager.update(sunoMusicData)
    }
}
