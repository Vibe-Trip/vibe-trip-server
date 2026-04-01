package com.vibetrip.vibetripserver.album.presentation.dto.response

import com.vibetrip.vibetripserver.album.domain.AlbumDetail
import java.time.LocalDate

data class AlbumDetailResponse(
    val title: String,
    val coverImageUrl: String,
    val region: String,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
    val resourceUrl: String = "",
) {
    companion object {
        fun from(albumDetail: AlbumDetail) =
            AlbumDetailResponse(
                title = albumDetail.album.title,
                coverImageUrl = albumDetail.album.coverImageUrl,
                region = albumDetail.album.region.value,
                travelStartDate = albumDetail.album.travelDate.startDate,
                travelEndDate = albumDetail.album.travelDate.endDate,
                resourceUrl = albumDetail.resourceUrl,
            )
    }
}
