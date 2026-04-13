package com.vibetrip.vibetripserver.album.domain

import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import com.vibetrip.vibetripserver.album.domain.vo.TravelDate

data class MusicInfo(
    val region: Region,
    val comment: Comment,
    val travelDate: TravelDate,
    val vocalGender: VocalGender,
    val genre: GenreType,
) {
    val regionValue: String
        get() = region.value

    val commentValue: String
        get() = comment.value
}
