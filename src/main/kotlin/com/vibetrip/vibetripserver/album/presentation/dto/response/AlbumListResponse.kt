package com.vibetrip.vibetripserver.album.presentation.dto.response

import com.vibetrip.vibetripserver.album.domain.Album
import java.time.LocalDate

data class AlbumListResponse(
    val albumId: Long,
    val title: String,
    val coverImageUrl: String,
    val region: String,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
    val previewLogImages: List<PreviewLogImage>,
    val logImageCount: Long,
) {
    companion object {
        fun from(
            album: Album,
            previewLogImages: List<PreviewLogImage>,
            logImageCount: Long,
        ) = AlbumListResponse(
            albumId = album.albumId,
            title = album.title,
            coverImageUrl = album.coverImageUrl,
            region = album.region.value,
            travelStartDate = album.travelDate.startDate,
            travelEndDate = album.travelDate.endDate,
            previewLogImages = previewLogImages,
            logImageCount = logImageCount,
        )
    }
}
