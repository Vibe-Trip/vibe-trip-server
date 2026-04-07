package com.vibetrip.vibetripserver.album.domain

import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Region
import com.vibetrip.vibetripserver.album.domain.vo.TravelDate
import com.vibetrip.vibetripserver.album.domain.vo.VocalOption
import java.time.LocalDate

data class NewAlbum(
    val memberKey: String,
    val region: Region,
    val comment: Comment,
    val travelDate: TravelDate,
    val vocalOption: VocalOption,
    val genre: GenreType,
) {
    companion object {
        fun of(
            memberKey: String,
            region: String,
            comment: String,
            travelStartDate: LocalDate,
            travelEndDate: LocalDate,
            withLyrics: Boolean,
            vocalGender: VocalGender = VocalGender.N,
            genre: GenreType,
        ) = NewAlbum(
            memberKey = memberKey,
            region = Region(region),
            comment = Comment(comment),
            travelDate = TravelDate(travelStartDate, travelEndDate),
            vocalOption = VocalOption(withLyrics, vocalGender),
            genre = genre,
        )
    }
}
