package com.vibetrip.vibetripserver.fixture

import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.VocalGender
import java.time.LocalDate

object AlbumFixture {
    fun newAlbum(
        memberKey: String = "member-key-123",
        region: String = "도쿄",
        comment: String = "행복했던 여행",
        travelStartDate: LocalDate = LocalDate.of(2026, 1, 1),
        travelEndDate: LocalDate = LocalDate.of(2026, 2, 1),
        withLyrics: Boolean = false,
        vocalGender: VocalGender = VocalGender.N,
        genre: GenreType = GenreType.CLASSICAL,
    ) = NewAlbum.of(
        memberKey = memberKey,
        region = region,
        comment = comment,
        travelStartDate = travelStartDate,
        travelEndDate = travelEndDate,
        withLyrics = withLyrics,
        vocalGender = vocalGender,
        genre = genre,
    )

    fun generatedMusic(
        title: String = "도쿄의 밤",
        resourceUrl: String = "https://mock-music-url.mp3",
    ) = AlbumMusic(title = title, resourceUrl = resourceUrl)
}
