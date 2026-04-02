package com.vibetrip.vibetripserver.album.domain

data class SunoRequest(
    val customMode: Boolean,
    val instrumental: Boolean,
    val model: String,
    val callBackUrl: String,
    val prompt: String,
    val style: String,
    val title: String,
    val vocalGender: String,
    val styleWeight: Float,
    val weirdnessConstraint: Float,
    val audioWeight: Float,
) {
    companion object {
        fun ofNoCustom(
            genre: GenreType,
            callBackUrl: String,
            prompt: String,
            vocalGender: VocalGender,
            model: SunoModel = SunoModel.V4,
            styleWeight: Float = 0.65f,
            weirdnessConstraint: Float = 0.65f,
            audioWeight: Float = 0.65f,
        ) = SunoRequest(
            customMode = false,
            instrumental = genre.withLyrics.not(),
            model = model.name,
            callBackUrl = callBackUrl,
            prompt = prompt,
            style = genre.name,
            title = "",
            vocalGender = vocalGender.name.lowercase(),
            styleWeight = styleWeight,
            weirdnessConstraint = weirdnessConstraint,
            audioWeight = audioWeight,
        )

        fun ofCustom(
            genre: GenreType,
            callBackUrl: String,
            prompt: String,
            title: String,
            vocalGender: VocalGender,
            model: SunoModel = SunoModel.V4,
            styleWeight: Float = 0.65f,
            weirdnessConstraint: Float = 0.65f,
            audioWeight: Float = 0.65f,
        ) = SunoRequest(
            customMode = true,
            instrumental = genre.withLyrics.not(),
            model = model.name,
            callBackUrl = callBackUrl,
            prompt = prompt,
            style = genre.name,
            title = title,
            vocalGender = vocalGender.name.lowercase(),
            styleWeight = styleWeight,
            weirdnessConstraint = weirdnessConstraint,
            audioWeight = audioWeight,
        )
    }
}
