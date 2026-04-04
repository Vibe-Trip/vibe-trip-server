package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.ImageAnalysis
import com.vibetrip.vibetripserver.album.domain.SunoMusicGenerateResponse
import com.vibetrip.vibetripserver.album.domain.SunoRequest
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

private const val SUNO_MUSIC_GENERATE_URL = "https://api.sunoapi.org/api/v1/generate"
private const val TOKEN_TYPE = "Bearer"

@Component
class MusicGenerator(
    @Value($$"${ai.suno.api-key}")
    private val aiApiKey: String,
    @Value($$"${ai.suno.callback-uri}")
    private val callbackUrl: String,
    @Qualifier("aiRestClient")
    private val restClient: RestClient,
) {
    fun generate(
        genre: GenreType,
        vocalGender: VocalGender,
        imageAnalysis: ImageAnalysis,
    ) = restClient
        .post()
        .uri(SUNO_MUSIC_GENERATE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION, "$TOKEN_TYPE $aiApiKey")
        .body(
            SunoRequest.ofCustom(
                genre = genre,
                style = imageAnalysis.musicStyle,
                callBackUrl = callbackUrl,
                prompt = imageAnalysis.lyrics,
                title = imageAnalysis.title,
                vocalGender = vocalGender,
            ),
        ).retrieve()
        .body(SunoMusicGenerateResponse::class.java) ?: throw AppException(ErrorType.SERVER_ERROR)
}
