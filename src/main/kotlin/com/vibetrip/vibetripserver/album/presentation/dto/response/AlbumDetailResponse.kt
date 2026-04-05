package com.vibetrip.vibetripserver.album.presentation.dto.response

import com.vibetrip.vibetripserver.album.domain.AlbumDetail
import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.VocalGender
import java.time.LocalDate

data class AlbumDetailResponse(
    val title: String,
    val coverImageUrl: String,
    val region: String,
    val comment: String,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
    val musicUrl: String = "",
    val genre: GenreType,
    val vocalGender: VocalGender,
    val withLyrics: Boolean,
) {
    companion object {
        fun from(albumDetail: AlbumDetail) =
            AlbumDetailResponse(
                title = albumDetail.album.title,
                coverImageUrl = albumDetail.album.coverImageUrl,
                region = albumDetail.album.region.value,
                comment = albumDetail.album.comment.value,
                travelStartDate = albumDetail.album.travelDate.startDate,
                travelEndDate = albumDetail.album.travelDate.endDate,
                musicUrl = albumDetail.musicUrl,
                genre = albumDetail.genre,
                vocalGender = albumDetail.vocalGender,
                withLyrics = albumDetail.withLyrics,
            )
    }
}
