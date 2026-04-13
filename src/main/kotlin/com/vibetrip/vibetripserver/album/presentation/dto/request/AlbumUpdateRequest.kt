package com.vibetrip.vibetripserver.album.presentation.dto.request

import com.vibetrip.vibetripserver.album.domain.EditAlbum
import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.VocalGender
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class AlbumUpdateRequest(
    val regenerateMusic: Boolean,
    @field:NotBlank
    val title: String,
    @field:NotBlank
    @field:Size(max = 25)
    val region: String,
    @field:NotNull
    val travelStartDate: LocalDate,
    @field:NotNull
    val travelEndDate: LocalDate,
    val withLyrics: Boolean = false,
    val vocalGender: VocalGender = VocalGender.N,
    @field:NotNull
    val genre: GenreType,
    @field:Size(max = 500)
    val comment: String = "",
) {
    fun toEditAlbum(memberKey: String) =
        EditAlbum.of(
            title = title,
            memberKey = memberKey,
            region = region,
            comment = comment,
            travelStartDate = travelStartDate,
            travelEndDate = travelEndDate,
            vocalGender = vocalGender,
            genre = genre,
        )
}
