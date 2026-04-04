package com.vibetrip.vibetripserver.album.presentation.dto.request

import com.vibetrip.vibetripserver.album.domain.EditAlbum
import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.VocalGender
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

data class AlbumUpdateRequest(
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
    fun toEditAlbum(
        album: Long,
        image: MultipartFile?,
    ) = EditAlbum.of(
        albumId = album,
        region = region,
        comment = comment,
        travelStartDate = travelStartDate,
        travelEndDate = travelEndDate,
        withLyrics = withLyrics,
        vocalGender = vocalGender,
        genre = genre,
        image = image,
    )
}
