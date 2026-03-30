package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.implement.AiProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumCoverImageProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumCreateResponse
import com.vibetrip.vibetripserver.support.paging.Cursorable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AlbumService(
    private val albumManager: AlbumManager,
    private val aiProcessor: AiProcessor,
    private val albumCoverImageProcessor: AlbumCoverImageProcessor,
) {
    @Transactional
    fun createAlbum(
        newAlbum: NewAlbum,
        coverImage: MultipartFile,
    ): AlbumCreateResponse {
        val coverImageUrl = albumCoverImageProcessor.imageUpload(coverImage)
        val gcsUri = albumCoverImageProcessor.toGcsUri(coverImageUrl)
        val albumId = albumManager.create(newAlbum, coverImageUrl)
        val imageKeywords = aiProcessor.analyzeImage(gcsUri)
        aiProcessor.generateTitle(albumId, newAlbum, imageKeywords)
        aiProcessor.generateMusic(albumId, newAlbum, imageKeywords)
        return AlbumCreateResponse(albumId)
    }

    fun findAlbums(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ) = albumManager.find(memberKey, cursorable)

    fun countAlbums(memberKey: String): Long = albumManager.count(memberKey)
}
