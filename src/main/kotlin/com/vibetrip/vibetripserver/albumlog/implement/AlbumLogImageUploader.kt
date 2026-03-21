package com.vibetrip.vibetripserver.albumlog.implement

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.repository.AlbumLogImageRepository
import com.vibetrip.vibetripserver.albumlog.domain.AlbumLogImages
import com.vibetrip.vibetripserver.albumlog.domain.ImageData
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import org.springframework.stereotype.Component

@Component
class AlbumLogImageUploader(
    private val googleImageUploader: GoogleImageUploader,
    private val albumLogImageRepository: AlbumLogImageRepository,
) {
    fun uploadImages(images: List<ImageData>, albumLogId: Long) =
        googleImageUploader.uploadImages(images).let { imageUrls ->
            imageUrls.forEach { albumLogImageRepository.save(AlbumLogImageEntity(it, albumLogId)) }
            AlbumLogImages(imageUrls)
        }

}