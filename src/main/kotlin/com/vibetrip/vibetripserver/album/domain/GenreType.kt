package com.vibetrip.vibetripserver.album.domain

enum class GenreType(
    val description: String,
    val withLyrics: Boolean,
) {
    // 보컬 있음
    POP("누구나 즐길 수 있는 대중적이고 산뜻한 리듬", true),
    K_POP("전 세계를 사로잡은 화려하고 트렌디한 사운드", true),
    BALLAD("감미로운 선율에 담긴 깊은 서사와 애절한 울림", true),
    HIP_HOP("강렬한 비트 위에 펼쳐지는 자유로운 리듬의 향연", true),
    R_AND_B("부드럽고 그루비한 보컬이 매력적인 소울풀한 감성", true),
    ROCK("심장을 울리는 강렬한 밴드 사운드와 뜨거운 에너지", true),
    CITY_POP("세련된 도시의 밤이 느껴지는 레트로한 도심 무드", true),
    EDM("심박수를 높이는 짜릿한 전자음과 페스티벌 분위기", true),
    LATIN_POP("정열적이고 태양처럼 뜨거운 댄서블한 라틴 리듬", true),
    COUNTRY("따뜻하고 정겨운 어쿠스틱 악기가 주는 향수", true),
    INDIE("나만 알고 싶은 담백하고 독창적인 감성과 개성", true),
    GOSPEL("풍성한 화음이 전하는 평온함과 영성 어린 위로", true),

    // 보컬 없음
    CLASSICAL("웅장하고 품격 있는 정통 오케스트라의 깊은 선율", false),
    LO_FI("나른한 오후, 일상의 소음이 섞인 편안하고 빈티지한 비트", false),
    JAZZ("세련된 선율과 자유로운 리듬이 만드는 여유로운 카페 분위기", false),
    AMBIENT("공간을 가득 채우는 몽환적이고 고요한 명상 같은 사운드", false),
    CINEMATIC("영화 속 한 장면처럼 서사적이고 웅장한 감동의 연주", false),
    NEW_AGE("지친 마음을 부드럽게 어루만지는 맑고 평온한 힐링 사운드", false),
    ACOUSTIC("악기 본연의 울림이 전하는 따뜻하고 순수한 날 것의 감성", false),
    ELECTRONIC("감각적인 합성음이 선사하는 세련되고 현대적인 도시 무드", false),
    BOSSA_NOVA("나른한 햇살 아래 여유로운 해변의 정취가 느껴지는 선율", false),
    CHILL_HOP("부드러운 그루브와 편안한 리듬이 공존하는 여유로운 휴식", false),
    TROPICAL_HOUSE("시원한 바닷바람처럼 청량하고 밝은 에너지가 가득한 사운드", false),
    TECHNO("반복적인 비트가 선사하는 강렬한 몰입감과 기계적인 미학", false),
}