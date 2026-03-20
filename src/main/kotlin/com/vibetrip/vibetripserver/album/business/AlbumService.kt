package com.vibetrip.vibetripserver.album.business

import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import com.vibetrip.vibetripserver.album.implement.AlbumManager
import com.vibetrip.vibetripserver.album.presentation.dto.AlbumCreateResponse
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AlbumService(
    private val albumManager: AlbumManager,
    private val albumMusicService: AlbumMusicService,
) {
    fun  create(newAlbum: NewAlbum, image: MultipartFile?): AlbumCreateResponse {
        val albumId = albumManager.create(newAlbum)
        albumMusicService.generate(NewAlbumMusic.of(albumId, newAlbum))
        return AlbumCreateResponse(albumId)
    }
}