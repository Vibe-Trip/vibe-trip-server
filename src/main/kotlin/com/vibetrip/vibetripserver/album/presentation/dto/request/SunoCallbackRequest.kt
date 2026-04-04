package com.vibetrip.vibetripserver.album.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.vibetrip.vibetripserver.album.domain.SunoMusicData

data class SunoCallbackRequest(
    val code: Int,
    val msg: String,
    val data: MusicGenerationData,
) {
    fun toSunoMusicData() =
        SunoMusicData(
            taskId = data.taskId,
            id = data.data[0].id,
            audioUrl = data.data[0].audioUrl,
            sourceAudioUrl = data.data[0].sourceAudioUrl ?: "",
            streamAudioUrl = data.data[0].streamAudioUrl ?: "",
            sourceStreamAudioUrl = data.data[0].sourceStreamAudioUrl ?: "",
            imageUrl = data.data[0].imageUrl ?: "",
            sourceImageUrl = data.data[0].sourceImageUrl ?: "",
            prompt = data.data[0].prompt,
            modelName = data.data[0].modelName,
            title = data.data[0].title,
            tags = data.data[0].tags,
            createTime = data.data[0].createTime,
            duration = data.data[0].duration,
        )
}

data class MusicGenerationData(
    val callbackType: String,
    @JsonProperty("task_id")
    val taskId: String,
    val data: List<GeneratedMusic>,
)

data class GeneratedMusic(
    val id: String,
    @JsonProperty("audio_url")
    val audioUrl: String,
    @JsonProperty("source_audio_url")
    val sourceAudioUrl: String?,
    @JsonProperty("stream_audio_url")
    val streamAudioUrl: String?,
    @JsonProperty("source_stream_audio_url")
    val sourceStreamAudioUrl: String?,
    @JsonProperty("image_url")
    val imageUrl: String?,
    @JsonProperty("source_image_url")
    val sourceImageUrl: String?,
    val prompt: String,
    @JsonProperty("model_name")
    val modelName: String,
    val title: String,
    val tags: String,
    val createTime: String,
    val duration: Double,
)
