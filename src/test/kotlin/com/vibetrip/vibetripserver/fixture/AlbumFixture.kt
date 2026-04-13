package com.vibetrip.vibetripserver.fixture

import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumEntity
import com.vibetrip.vibetripserver.album.dataaccess.entity.AlbumMusicEntity
import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.EditAlbum
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
        vocalGender: VocalGender = VocalGender.N,
        genre: GenreType = GenreType.CLASSICAL,
    ) = NewAlbum.of(
        memberKey = memberKey,
        region = region,
        comment = comment,
        travelStartDate = travelStartDate,
        travelEndDate = travelEndDate,
        vocalGender = vocalGender,
        genre = genre,
    )

    fun editAlbum(
        title: String = "title",
        memberKey: String = "member-key-123",
        region: String = "도쿄",
        comment: String = "행복했던 여행",
        travelStartDate: LocalDate = LocalDate.of(2026, 1, 1),
        travelEndDate: LocalDate = LocalDate.of(2026, 2, 1),
        vocalGender: VocalGender = VocalGender.N,
        genre: GenreType = GenreType.CLASSICAL,
    ) = EditAlbum.of(
        title = title,
        memberKey = memberKey,
        region = region,
        comment = comment,
        travelStartDate = travelStartDate,
        travelEndDate = travelEndDate,
        vocalGender = vocalGender,
        genre = genre,
    )

    fun albumEntity(
        id: Long? = null,
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
        musicUrl: String = "https://mock-music-url.mp3",
    ) = AlbumMusicEntity(
        title = "도쿄의 밤",
        musicUrl = musicUrl,
        genre = GenreType.CLASSICAL,
        withLyrics = false,
        albumId = albumId,
        taskId = "",
    )

    fun generatedMusic(
        title: String = "도쿄의 밤",
        musicUrl: String = "https://mock-music-url.mp3",
    ) = AlbumMusic(title = title, musicUrl = musicUrl)
}
