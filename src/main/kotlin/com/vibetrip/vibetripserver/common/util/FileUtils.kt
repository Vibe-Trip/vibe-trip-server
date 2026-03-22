package com.vibetrip.vibetripserver.common.util

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import java.util.UUID

private val IMAGE_CONTENT_TYPES =
    listOf(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp",
        "image/svg+xml",
    )

fun generateUUIDFileName(fileName: String) = "${UUID.randomUUID()}_${fileName.replace("\\s".toRegex(), "_")}"

fun validateImageContentType(contentType: String?): String {
    if (contentType !in IMAGE_CONTENT_TYPES) {
        throw AppException(ErrorType.INVALID_IMAGE_TYPE)
    }

    return contentType!!
}
