package com.vibetrip.vibetripserver.album.implement

import com.vibetrip.vibetripserver.common.domain.ImageData
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.storage.GoogleImageUploader
import com.vibetrip.vibetripserver.common.util.validateImageContentType
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

private const val MAX_COVER_IMAGE_SIZE = 10 * 1024 * 1024L

@Component
class AlbumCoverImageProcessor(
    private val googleImageUploader: GoogleImageUploader,
) {
    fun imageUpload(image: MultipartFile): String {
        val imageData = validateAndConvert(image)
        return googleImageUploader.uploadImage(imageData)
    }

    private fun validateAndConvert(image: MultipartFile): ImageData {
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
