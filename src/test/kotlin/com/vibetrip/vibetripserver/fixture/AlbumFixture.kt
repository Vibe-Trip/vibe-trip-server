package com.vibetrip.vibetripserver.fixture

import com.vibetrip.vibetripserver.album.domain.GeneratedMusic
import com.vibetrip.vibetripserver.album.domain.NewAlbum
import com.vibetrip.vibetripserver.album.domain.NewAlbumMusic
import java.time.LocalDate

object AlbumFixture {
    fun newAlbum(
        memberKey: String = "member-key-123",
        region: String = "도쿄",
        comment: String? = "행복했던 여행",
        travelStartDate: LocalDate = LocalDate.of(2026, 1, 1),
        travelEndDate: LocalDate = LocalDate.of(2026, 2, 1),
        withLyrics: Boolean = false,
        vocalGender: String? = null,
        genre: String = "POP",
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

    fun newAlbumMusic(
        albumId: Long = 1L,
        newAlbum: NewAlbum = newAlbum(),
    ) = NewAlbumMusic.of(albumId, newAlbum)

    fun generatedMusic(
        title: String = "도쿄의 밤",
        resourceUrl: String = "https://mock-music-url.mp3",
    ) = GeneratedMusic(title = title, resourceUrl = resourceUrl)
}
