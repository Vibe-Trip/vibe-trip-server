package com.vibetrip.vibetripserver.fixture

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
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

    fun albumEntity(
        id: Long = 1L,
        memberKey: String = "member-key-123",
        title: String = "도쿄의 밤",
        coverImageUrl: String = "https://storage.googleapis.com/test.jpg",
        region: String = "도쿄",
        travelStartDate: LocalDate = LocalDate.of(2026, 1, 1),
        travelEndDate: LocalDate = LocalDate.of(2026, 2, 1),
    ) = AlbumEntity(
        memberKey = memberKey,
        title = title,
        coverImageUrl = coverImageUrl,
        region = region,
        travelStartDate = travelStartDate,
        travelEndDate = travelEndDate,
    ).apply { this.id = id }

    fun albumMusicEntity(
        albumId: Long = 1L,
        resourceUrl: String = "https://mock-music-url.mp3",
    ) = AlbumMusicEntity(
        title = "도쿄의 밤",
        resourceUrl = resourceUrl,
        genre = GenreType.CLASSICAL,
        withLyrics = false,
        albumId = albumId,
    )

    fun generatedMusic(
        title: String = "도쿄의 밤",
        resourceUrl: String = "https://mock-music-url.mp3",
    ) = AlbumMusic(title = title, resourceUrl = resourceUrl)
}
