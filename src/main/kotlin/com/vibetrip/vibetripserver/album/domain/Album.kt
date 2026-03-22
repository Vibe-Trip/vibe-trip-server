package com.vibetrip.vibetripserver.album.domain

import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import java.time.LocalDate

data class Album(
    val albumId: Long,
    val title: String?,
    val coverImageUrl: String,
    val region: Region,
    val comment: Comment?,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
    val memberKey: String,
) {
    companion object {
        fun of(
            albumId: Long,
            title: String?,
            coverImageUrl: String,
            region: String,
            comment: String?,
            travelStartDate: LocalDate,
            travelEndDate: LocalDate,
            memberKey: String,
        ) = Album(
            albumId = albumId,
            title = title,
            coverImageUrl = coverImageUrl,
            region = Region(region),
            comment = comment?.let { Comment(it) },
            travelStartDate = travelStartDate,
            travelEndDate = travelEndDate,
            memberKey = memberKey,
        )
    }
}
