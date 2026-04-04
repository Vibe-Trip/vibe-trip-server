package com.vibetrip.vibetripserver.album.domain

import com.vibetrip.vibetripserver.album.domain.vo.Comment
import com.vibetrip.vibetripserver.album.domain.vo.Genre
import com.vibetrip.vibetripserver.album.domain.vo.Region
import com.vibetrip.vibetripserver.album.domain.vo.TravelDate
import com.vibetrip.vibetripserver.album.domain.vo.VocalOption
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

data class EditAlbum(
    val albumId: Long,
    val region: Region,
    val comment: Comment,
    val travelDate: TravelDate,
    val vocalOption: VocalOption,
    val genre: Genre,
    val image: MultipartFile,
) {
    companion object {
        fun of(
            albumId: Long,
            region: String,
            comment: String,
            travelStartDate: LocalDate,
            travelEndDate: LocalDate,
            withLyrics: Boolean,
            vocalGender: VocalGender,
            genre: GenreType,
            image: MultipartFile,
        ) = EditAlbum(
            albumId = albumId,
            region = Region(region),
            comment = Comment(comment),
            travelDate = TravelDate(travelStartDate, travelEndDate),
            vocalOption = VocalOption(withLyrics, vocalGender),
            genre = Genre.of(genre, withLyrics),
            image = image,
        )
    }
}
