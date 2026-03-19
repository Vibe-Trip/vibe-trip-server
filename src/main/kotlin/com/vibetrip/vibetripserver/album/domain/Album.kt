package com.vibetrip.vibetripserver.album.domain

import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import java.time.LocalDate

data class Album(
    val id: Long?,
    val memberKey: String?,
    val title: String,
    val coverImageUrl: String,
    val region: Region,
    val comment: Comment?,
    val travelStartDate: LocalDate,
    val travelEndDate: LocalDate,
)