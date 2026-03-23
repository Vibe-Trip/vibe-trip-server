package com.vibetrip.vibetripserver.album.domain.vo

import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType

@JvmInline
value class Genre private constructor(
    val value: GenreType,
) {
    companion object {
        fun of(genreType: GenreType, withLyrics: Boolean): Genre{
            if(genreType.withLyrics!=withLyrics) throw AppException(ErrorType.INVALID_GENRE_LYRICS_MISMATCH)
            return Genre(genreType)
        }
    }
}