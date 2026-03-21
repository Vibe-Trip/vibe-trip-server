package com.vibetrip.vibetripserver.album.domain

import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import java.time.LocalDate

data class Album(
    val albumId: Long,
    val memberKey: String,
    val title: String?,
    val coverImageUrl: String?,
    val region: Region,
    val comment: Comment?,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
) {
    companion object {
        fun of(
            albumId: Long,
            memberKey: String,
            title: String?,
            coverImageUrl: String?,
            region: String,
            comment: String?,
            travelStartDate: LocalDate,
            travelEndDate: LocalDate,
        ) = Album(
            albumId = albumId,
            memberKey = memberKey,
            title = title,
            coverImageUrl = coverImageUrl,
            region = Region(region),
            comment = comment?.let { Comment(it) },
            travelStartDate = travelStartDate,
            travelEndDate = travelEndDate,
        )
    }
}
