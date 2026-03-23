package com.vibetrip.vibetripserver.common.storage

import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.vibetrip.vibetripserver.common.domain.ImageData
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.util.generateUUIDFileName
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private const val GCS_HOST = "https://storage.googleapis.com/"

@Component
class GoogleImageUploader(
    private val storage: Storage,
    @Value($$"${spring.cloud.gcp.storage.bucket}")
    private val bucketName: String,
) {
    fun uploadImage(image: ImageData): String {
        val imageFileName = generateUUIDFileName(image.originalFilename)

        return try {
            storage.createFrom(
                BlobInfo
                    .newBuilder(bucketName, imageFileName)
                    .setContentType(image.contentType)
                    .build(),
                image.data,
            )

            "$GCS_HOST$bucketName/$imageFileName"
        } catch (e: Exception) {
            throw AppException(ErrorType.IMAGE_UPLOAD_FAILED, e.message)
        }
    }
}
