package com.vibetrip.vibetripserver.album.presentation.dto.request

import com.vibetrip.vibetripserver.album.domain.NewAlbum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class AlbumCreateRequest(
    @field:NotBlank
    @field:Size(max = 15)
    val region: String,
    @field:NotNull
    val travelStartDate: LocalDate,
    @field:NotNull
    val travelEndDate: LocalDate,
    val withLyrics: Boolean = false,
    val vocalGender: String? = null,
    @field:NotBlank
    val genre: String,
    @field:Size(max = 500)
    val comment: String? = null,
) {
    fun toNewAlbum(memberKey: String) =
        NewAlbum.of(
            memberKey = memberKey,
            region = region,
            comment = comment,
            travelStartDate = travelStartDate,
            travelEndDate = travelEndDate,
            withLyrics = withLyrics,
            vocalGender = vocalGender,
            genre = genre,
        )
}
