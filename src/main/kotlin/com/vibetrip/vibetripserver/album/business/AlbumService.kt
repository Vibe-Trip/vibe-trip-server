package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.implement.AiProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumFinder
import com.vibetrip.vibetripserver.album.implement.AlbumCoverImageProcessor
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumCreateResponse
import com.vibetrip.vibetripserver.support.paging.Cursorable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AlbumService(
    private val albumManager: AlbumManager,
    private val aiProcessor: AiProcessor,
    private val albumFinder: AlbumFinder,
    private val albumCoverImageProcessor: AlbumCoverImageProcessor,
) {
    fun create(
        newAlbum: NewAlbum,
        image: MultipartFile,
    ): AlbumCreateResponse {
        val coverImageUrl = albumCoverImageProcessor.imageUpload(image)
        val gcsUri = "" // TODO: GCS URI (gs://bucket/filename)
        val albumId = albumManager.create(newAlbum, coverImageUrl)
        val imageKeywords = aiProcessor.analyzeImage(gcsUri)
        aiProcessor.generateTitle(albumId, newAlbum, imageKeywords)
        aiProcessor.generateMusic(albumId, newAlbum, imageKeywords)
        return AlbumCreateResponse(albumId)
    }

    fun findAlbums(
        memberKey: String,
        cursorable: Cursorable<Long>,
    ) = albumFinder.findAllByMemberKey(memberKey, cursorable)

    fun countAlbums(memberKey: String): Long = albumFinder.countByMemberKey(memberKey)
}
