package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.common.domain.ImageData
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

private const val MAX_COVER_IMAGE_SIZE = 10 * 1024 * 1024L

@Component
class AlbumCoverImageProcessor(
    private val googleImageUploader: GoogleImageUploader,
    @Value("\${spring.cloud.gcp.storage.bucket}")
    private val bucketName: String,
) {
    fun imageUpload(coverImage: MultipartFile): String {
        val imageData = validateAndConvert(coverImage)
        return googleImageUploader.uploadImage(imageData)
    }

    fun toGcsUri(coverImageUrl: String): String = "gs://$bucketName/${coverImageUrl.substringAfterLast("/")}"

    private fun validateAndConvert(coverImage: MultipartFile): ImageData {
        val contentType = validateImageContentType(coverImage.contentType)

        return ImageData(
            coverImage.inputStream,
            contentType,
            coverImage.originalFilename!!,
        )
    }
}
