package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.common.exception.AppException
import com.vibetrip.vibetripserver.common.exception.ErrorType
import com.vibetrip.vibetripserver.common.log.logger
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.content.Media
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile

@Component
class ImageAnalyzer(
    private val chatClient: ChatClient,
    @Value("classpath:prompts/image-analysis-prompt.st")
    private val imageAnalysisPromptTemplate: Resource,
) {
    fun analyze(image: MultipartFile): String {
        val imageMedia = Media(MimeTypeUtils.IMAGE_JPEG, image.resource)
        val prompt = PromptTemplate(imageAnalysisPromptTemplate).render()
        logger.info { prompt }
        val message =
            UserMessage
                .builder()
                .media(imageMedia)
                .text(prompt)
                .build()

        return chatClient
            .prompt()
            .messages(message)
            .call()
            .content() ?: throw AppException(ErrorType.SERVER_ERROR)
    }
}
