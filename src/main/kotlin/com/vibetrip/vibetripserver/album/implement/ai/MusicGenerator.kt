package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.AlbumMusic
import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.SunoRequest
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

private const val REGION = "region"
private const val COMMENT = "comment"
private const val GENRE = "genre"
private const val VOCAL_GENDER = "vocalGender"
private const val WITH_LYRICS = "withLyrics"
private const val IMAGE_KEYWORDS = "keywords"

private const val SUNO_MUSIC_GENERATE_URL = "https://api.sunoapi.org/api/v1/generate"
private const val TOKEN_TYPE = "Bearer"

@Component
class MusicGenerator(
    @Value("classpath:prompts/music-prompt.st")
    private val musicPromptTemplate: Resource,
    @Value($$"${ai.suno.api-key}")
    private val aiApiKey: String,
    @Value($$"${ai.suno.callback-uri}")
    private val callbackUrl: String,
    @Qualifier("aiRestClient")
    private val restClient: RestClient,
) {
    fun generateMusic(
        region: String,
        comment: String,
        genre: GenreType,
        vocalGender: VocalGender,
        imageKeywords: String,
    ): AlbumMusic {
        val prompt =
            PromptTemplate(musicPromptTemplate).render(
                mapOf(
                    REGION to region,
                    COMMENT to comment,
                    GENRE to genre,
                    WITH_LYRICS to genre.withLyrics,
                    VOCAL_GENDER to vocalGender.name,
                    IMAGE_KEYWORDS to imageKeywords,
                ),
            )

        val response =
            restClient
                .post()
                .uri(SUNO_MUSIC_GENERATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "$TOKEN_TYPE $aiApiKey")
                .body(
                    SunoRequest.ofNoCustom(
                        genre = genre,
                        callBackUrl = callbackUrl,
                        prompt = prompt,
                        vocalGender = vocalGender,
                    ),
                ).retrieve()
                .body(String::class.java)

        logger.info { "music: $response" }
        // TODO: Suno API 연동 (region + comment + genre + vocalGender + withLyrics + imageKeywords → 음악 생성)
        return AlbumMusic(title = "", resourceUrl = "")
    }
}
