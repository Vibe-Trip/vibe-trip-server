package com.vibetrip.vibetripserver.album.domain

enum class GenreType(
    val genre: String,
    val description: String,
    val withLyrics: Boolean,
) {
    // 보컬 있음
    POP("Pop", "밝고 트렌디한 대중 음악", true),
    K_POP("K-Pop", "에너지 넘치고 화려한 아이돌 감성", true),
    J_POP("J-Pop", "청량하고 맑은 일본 감성의 음악", true),
    ACOUSTIC("Acoustic", "따뜻하고 편안한 통기타 감성", true),
    BALLAD("Ballad", "추억과 감성을 담은 잔잔한 음악", true),
    INDIE("Indie", "나만의 취향을 담은 감성적인 음악", true),
    ROCK("Rock", "시원하고 자유로운 밴드 사운드", true),
    JAZZ("Jazz", "재즈바 같은 낭만적인 분위기", true),
    R_AND_B("R&B", "부드럽고 세련된 감성의 그루브 음악", true),

    // 보컬 없음
    LO_FI("Lofi", "휴식에 어울리는 잔잔한 비트", false),
    AMBIENT("Ambient", "몽환적이고 신비로운 분위기의 음악", false),
    CINEMATIC("Cinematic", "영화 같은 웅장한 분위기의 음악", false),
    NEW_AGE("New Age", "잔잔한 피아노 중심의 편안한 음악", false),
    JAZZ_NO_VOCAL("Jazz", "재즈바 같은 여유롭고 낭만적인 연주", false),
    BOSSA_NOVA("Bossa Nova", "햇살 가득한 카페 같은 감성", false),
    TROPICAL_HOUSE("Tropical House", "바다와 여행이 떠오르는 시원한 리듬", false),
    DEEP_HOUSE("Deep House", "리듬감 있게 몰입되는 세련된 전자 음악", false),
    CHILLOUT("Chillout", "노을과 휴식에 어울리는 부드러운 감성 음악", false),
}
