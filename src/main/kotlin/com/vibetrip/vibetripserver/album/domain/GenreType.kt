package com.vibetrip.vibetripserver.album.domain

enum class GenreType(
    val withLyrics: Boolean,
) {
    // 보컬 있음
    POP(true),
    K_POP(true),
    J_POP(true),
    LATIN(true),
    R_AND_B(true),
    ROCK(true),
    COUNTRY(true),
    ACOUSTIC(true),
    INDIE(true),
    BALLAD(true),
    CLASSICAL(true),
    JAZZ(true),

    // 보컬 없음
    LO_FI(false),
    AMBIENT(false),
    CINEMATIC(false),
    NEW_AGE(false),
    CHILL_OUT(false),
    BOSSA_NOVA(false),
    TROPICAL_HOUSE(false),
    POST_ROCK(false),
    CLASSIC_SOLO(false),
    ACOUSTIC_FOLK(false),
    DEEP_HOUSE(false),
}