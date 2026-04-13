package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.GenreType
import com.vibetrip.vibetripserver.album.domain.ImageAnalysis
import com.vibetrip.vibetripserver.album.domain.VocalGender
import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.content.Media
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.util.MimeTypeUtils

private const val REGION = "region"
private const val GENRE = "genre"
private const val COMMENT = "comment"
private const val GENDER = "gender"

@Component
class ImageAnalyzer(
    private val chatClient: ChatClient,
    @Value("classpath:prompts/image-analysis-prompt.st")
    private val imageAnalysisPromptTemplate: Resource,
) {
    fun analyze(
        imageBytes: ByteArray,
        region: String,
        genre: GenreType,
        vocalGender: VocalGender,
        comment: String,
    ): ImageAnalysis {
        val imageMedia = Media(MimeTypeUtils.IMAGE_JPEG, ByteArrayResource(imageBytes))
        val prompt =
            PromptTemplate(imageAnalysisPromptTemplate).render(
                mapOf(
                    REGION to region,
                    GENRE to genre,
                    GENDER to vocalGender.value,
                    COMMENT to comment,
                ),
            )
        val message =
            UserMessage
                .builder()
                .media(imageMedia)
                .text(prompt)
                .build()

        val analysis =
            chatClient
                .prompt()
                .messages(message)
                .call()
                .entity(ImageAnalysis::class.java) ?: throw AppException(ErrorType.SERVER_ERROR)

        logger.info { "imageAnalysis: $analysis" }
        return analysis
    }
}
