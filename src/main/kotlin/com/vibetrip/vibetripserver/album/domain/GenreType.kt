package com.vibetrip.vibetripserver.album.domain

enum class GenreType(
    val genre: String,
    val description: String,
    val withLyrics: Boolean,
) {
    // 보컬 있음
    POP("Pop", "세련된 글로벌 트렌디 사운드의 정석", true),
    K_POP("K-Pop", "화려하고 에너제틱한 주인공의 기분", true),
    J_POP("J-Pop", "도심 산책에 어울리는 청량하고 맑은 무드", true),
    LATIN("Latin", "휴양지의 열정을 더하는 이국적인 리듬", true),
    R_AND_B("R&B", "도시의 밤을 적시는 감각적인 그루브", true),
    ROCK("Rock", "자유로운 에너지가 폭발하는 드라이브 감성", true),
    COUNTRY("Country", "자연 속을 달리는 편안한 로드트립의 여유", true),
    ACOUSTIC("Acoustic", "통기타 선율이 전하는 따뜻하고 진솔한 위로", true),
    INDIE("Indie", "나만의 취향을 담은 독특하고 힙한 무드", true),
    BALLAD("Ballad", "잊지 못할 여행의 추억을 담은 애틋한 선율", true),
    CLASSICAL("Classical", "대자연의 웅장함을 담은 고품격 대서사시", true),
    JAZZ("Jazz", "루프탑 야경에 어울리는 여유롭고 낭만적인 밤", true),

    // 보컬 없음
    LO_FI("Lo-Fi", "나른한 오후의 여유를 담은 편안하고 낮은 비트", false),
    AMBIENT("Ambient", "공간을 가득 채우는 몽환적이고 신비로운 울림", false),
    CINEMATIC("Cinematic", "영화 속 주인공이 된 듯한 웅장하고 드라마틱한 감동", false),
    NEW_AGE("New Age", "맑은 피아노 선율이 전하는 평온하고 순수한 휴식", false),
    CHILLOUT("Chillout", "복잡한 생각을 비워주는 세련된 휴양지의 무드", false),
    JAZZ_NO_VOCAL("Jazz", "세련되고 낭만적인 밤의 여유를 담은 즉흥 선율", false),
    BOSSA_NOVA("Bossa Nova", "햇살 가득한 해변을 걷는 듯 가볍고 경쾌한 리듬", false),
    TROPICAL_HOUSE("Tropical House", "파도 소리가 들리는 듯 시원하고 청량한 여름의 설렘", false),
    POST_ROCK("Post-Rock", "서서히 차오르는 서정적인 감정의 깊은 파동", false),
    CLASSIC_SOLO("Classic Solo", "우아한 악기 선율로 완성하는 품격 있는 기록", false),
    ACOUSTIC_FOLK("Acoustic Folk", "소박한 기타 연주에 담긴 따뜻하고 다정한 자연의 향기", false),
    DEEP_HOUSE("Deep House", "세련된 도시의 밤거리를 닮은 절제된 감각의 비트", false),
}
