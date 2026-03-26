package com.vibetrip.vibetripserver.fixture

import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageEntity
import com.vibetrip.vibetripserver.albumlog.dataaccess.entity.AlbumLogImageOutbox
import com.vibetrip.vibetripserver.albumlog.domain.EditAlbumLog
import com.vibetrip.vibetripserver.albumlog.domain.NewAlbumLog
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile

object AlbumLogFixture {
    fun newAlbumLog(
        description: String = "테스트 설명",
        albumId: Long = 1L,
    ) = NewAlbumLog.of(
        description = description,
        albumId = albumId,
    )

    fun editAlbumLog(
        id: Long = 1L,
        description: String = "수정된 설명",
        albumId: Long = 1L,
        newImages: List<MultipartFile> = emptyList(),
        removeImageIds: List<Long> = emptyList(),
    ) = EditAlbumLog.of(
        id = id,
        description = description,
        albumId = albumId,
        newImages = newImages,
        removeImageIds = removeImageIds,
    )

    fun albumLogEntity(
        id: Long = 1L,
        description: String = "테스트 설명",
        albumId: Long = 1L,
    ) = AlbumLogEntity(
        description = description,
        albumId = albumId,
    ).apply { this.id = id }

    fun albumLogImageEntity(
        id: Long = 1L,
        imageUrl: String = "https://storage.example.com/test.jpg",
        albumLogId: Long = 1L,
    ) = AlbumLogImageEntity(
        imageUrl = imageUrl,
        albumLogId = albumLogId,
    ).apply { this.id = id }

    fun albumLogImageOutbox(
        id: Long = 1L,
        tempFilePath: String = "/tmp/uploads/test.jpg",
        contentType: String = "image/jpeg",
        originalFileName: String = "test.jpg",
        albumLogId: Long = 1L,
    ) = AlbumLogImageOutbox(
        tempFilePath = tempFilePath,
        contentType = contentType,
        originalFileName = originalFileName,
        albumLogId = albumLogId,
    ).apply { this.id = id }

    fun mockMultipartFile(
        name: String = "images",
        originalFilename: String = "test.jpg",
        contentType: String = "image/jpeg",
        content: ByteArray = "test-image-data".toByteArray(),
    ) = MockMultipartFile(
        name,
        originalFilename,
        contentType,
        content,
    )

    fun mockMultipartFiles(count: Int = 2) =
        (1..count).map {
            mockMultipartFile(originalFilename = "test$it.jpg")
        }
}
