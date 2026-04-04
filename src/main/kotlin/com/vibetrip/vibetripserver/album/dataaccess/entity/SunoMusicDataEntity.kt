package com.vibetrip.vibetripserver.album.dataaccess.entity

import com.vibetrip.vibetripserver.album.domain.SunoMusicData
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "suno_music_data")
class SunoMusicDataEntity(
    @Column(nullable = false)
    val taskId: String,
    @Column(nullable = false)
    val id: String,
    @Column(nullable = false)
    val audioUrl: String,
    @Column(nullable = false)
    val sourceAudioUrl: String,
    @Column(nullable = false)
    val streamAudioUrl: String,
    @Column(nullable = false)
    val sourceStreamAudioUrl: String,
    @Column(nullable = false)
    val imageUrl: String,
    @Column(nullable = false)
    val sourceImageUrl: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val prompt: String,
    @Column(nullable = false)
    val modelName: String,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val tags: String,
    @Column(nullable = false)
    val createTime: String,
    @Column(nullable = false)
    val duration: Double,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var sunoMusicDataId: Long? = null,
) {
    companion object {
        fun from(sunoMusicData: SunoMusicData) =
            SunoMusicDataEntity(
                taskId = sunoMusicData.taskId,
                id = sunoMusicData.id,
                audioUrl = sunoMusicData.audioUrl,
                sourceAudioUrl = sunoMusicData.sourceAudioUrl,
                streamAudioUrl = sunoMusicData.streamAudioUrl,
                sourceStreamAudioUrl = sunoMusicData.sourceStreamAudioUrl,
                imageUrl = sunoMusicData.imageUrl,
                sourceImageUrl = sunoMusicData.sourceImageUrl,
                prompt = sunoMusicData.prompt,
                modelName = sunoMusicData.modelName,
                title = sunoMusicData.title,
                tags = sunoMusicData.tags,
                createTime = sunoMusicData.createTime,
                duration = sunoMusicData.duration,
            )
    }
}
