package com.vibetrip.vibetripserver.album.domain

import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import com.vibetrip.vibetripserver.album.domain.vo.TravelDate
import java.time.LocalDate

data class NewAlbum(
    val memberKey: String,
    val region: Region,
    val comment: Comment,
    val travelDate: TravelDate,
    val vocalGender: VocalGender,
    val genre: GenreType,
) {
    companion object {
        fun of(
            memberKey: String,
            region: String,
            comment: String,
            travelStartDate: LocalDate,
            travelEndDate: LocalDate,
            vocalGender: VocalGender = VocalGender.N,
            genre: GenreType,
        ) = NewAlbum(
            memberKey = memberKey,
            region = Region(region),
            comment = Comment(comment),
            travelDate = TravelDate(travelStartDate, travelEndDate),
            vocalGender = vocalGender,
            genre = genre,
        )
    }

    fun toMusicInfo() =
        MusicInfo(
            region = region,
            comment = comment,
            travelDate = travelDate,
            vocalGender = vocalGender,
            genre = genre,
        )
}
