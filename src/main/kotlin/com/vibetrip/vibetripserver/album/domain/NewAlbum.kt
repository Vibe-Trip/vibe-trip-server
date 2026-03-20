package com.vibetrip.vibetripserver.album.domain

import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import java.time.LocalDate

data class NewAlbum(
    val memberKey: String,
    val region: Region,
    val comment: Comment?,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
    val withLyrics: Boolean,
    val vocalGender: String?,
    val genre: String,
) {
    companion object {
        fun of(
            memberKey: String,
            region: String,
            comment: String?,
            travelStartDate: LocalDate,
            travelEndDate: LocalDate,
            withLyrics: Boolean,
            vocalGender: String?,
            genre: String,
        ) = NewAlbum(
            memberKey = memberKey,
            region = Region(region),
            comment = comment?.let { Comment(it) },
            travelStartDate = travelStartDate,
            travelEndDate = travelEndDate,
            withLyrics = withLyrics,
            vocalGender = vocalGender,
            genre = genre,
        )
    }

    val regionValue: String get() = region.value
    val commentValue: String? get() = comment?.value
}
