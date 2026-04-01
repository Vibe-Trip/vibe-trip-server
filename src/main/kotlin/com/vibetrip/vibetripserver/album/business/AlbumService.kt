package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.AlbumDetail
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.implement.AiProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumCoverImageProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.implement.AlbumMemberManager
import com.vibetrip.vibetripserver.album.implement.AlbumMusicManager
import com.vibetrip.vibetripserver.support.paging.Cursorable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AlbumService(
    private val albumMemberManager: AlbumMemberManager,
    private val albumMusicManager: AlbumMusicManager,
    private val albumManager: AlbumManager,
    private val aiProcessor: AiProcessor,
    private val albumCoverImageProcessor: AlbumCoverImageProcessor,
) {
    @Transactional
    fun createAlbum(
        newAlbum: NewAlbum,
        coverImage: MultipartFile,
    ): Long {
        val coverImageUrl = albumCoverImageProcessor.imageUpload(coverImage)
        val gcsUri = albumCoverImageProcessor.toGcsUri(coverImageUrl)
        val albumId = albumManager.create(newAlbum, coverImageUrl)
        val imageKeywords = aiProcessor.analyzeImage(gcsUri)
        aiProcessor.generateTitle(albumId, newAlbum, imageKeywords)
        aiProcessor.generateMusic(albumId, newAlbum, imageKeywords)
        return albumId
    }

    fun getAlbumCount(memberKey: String) = albumManager.count(memberKey)

    @Transactional(readOnly = true)
    fun findAlbums(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ) = albumManager.find(memberKey, cursorable)

    @Transactional(readOnly = true)
    fun findAlbum(
        albumId: Long,
        memberKey: String,
    ): AlbumDetail {
        albumMemberManager.validateMember(albumId, memberKey)
        val album = albumManager.findAlbum(albumId)
        val resourceUrl = albumMusicManager.getResourceUrl(albumId)

        return AlbumDetail(album, resourceUrl)
    }

    fun countAlbums(memberKey: String): Long = albumManager.count(memberKey)
}
