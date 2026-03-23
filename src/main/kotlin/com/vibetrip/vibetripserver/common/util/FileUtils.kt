package com.vibetrip.vibetripserver.common.util

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
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

object TempFileStorage {
    private val tempDir = Path.of(System.getProperty("java.io.tmpdir"), "uploads")

    init {
        Files.createDirectories(tempDir)
    }

    fun save(file: MultipartFile): Path {
        val originalFileName = file.originalFilename ?: throw AppException(ErrorType.IMAGE_UPLOAD_FAILED)
        val fileName = generateUUIDFileName(originalFileName)
        val path = tempDir.resolve(fileName)
        file.transferTo(path)
        return path
    }

    fun delete(filePath: Path) {
        Files.deleteIfExists(filePath)
    }
}
