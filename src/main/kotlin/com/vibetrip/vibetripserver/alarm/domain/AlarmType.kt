package com.vibetrip.vibetripserver.alarm.domain

enum class AlarmType(
    val title: String,
    val description: String,
) {
    CREATING("앨범을 생성하는 중입니다", "나만의 음악이 곧 탄생합니다. 완료되면 바로 알려드릴게요"),
    COMPLETED("앨범 생성 완료!", "세상에 하나뿐인 '%s'이 완성되었습니다. 지금 바로 완성된 음악을 감상해보세요"),
    FAILED("앨범 생성에 실패했습니다", "%s으로 생성이 실패했습니다. 앨범 만들기를 다시 시도해 볼까요?"),
}
