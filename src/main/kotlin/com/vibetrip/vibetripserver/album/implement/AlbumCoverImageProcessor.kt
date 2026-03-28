package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.common.domain.ImageData
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

private const val MAX_COVER_IMAGE_SIZE = 10 * 1024 * 1024L
private const val MAX_COVER_IMAGE_COUNT = 1

@Component
class AlbumCoverImageProcessor(
    private val googleImageUploader: GoogleImageUploader,
    @Value("\${spring.cloud.gcp.storage.bucket}")
    private val bucketName: String,
) {
    fun imageUpload(coverImage: List<MultipartFile>): String {
        val imageData = validateAndConvert(coverImage)
        return googleImageUploader.uploadImage(imageData)
    }

    fun toGcsUri(coverImageUrl: String): String = "gs://$bucketName/${coverImageUrl.substringAfterLast("/")}"

    private fun validateAndConvert(coverImage: List<MultipartFile>): ImageData {
        val image =
            coverImage.takeIf { it.size == MAX_COVER_IMAGE_COUNT }?.get(0)
                ?: throw AppException(ErrorType.INVALID_IMAGE_COUNT)
        val contentType = validateImageContentType(image.contentType)

        if (image.size > MAX_COVER_IMAGE_SIZE) {
            throw AppException(ErrorType.INVALID_IMAGE_SIZE)
        }

        return ImageData(
            image.inputStream,
            contentType,
            image.originalFilename!!,
        )
    }
}
