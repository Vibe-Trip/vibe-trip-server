package com.vibetrip.vibetripserver.album.implement.ai

import com.vibetrip.vibetripserver.album.domain.vo.Title
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

private const val REGION = "region"
private const val COMMENT = "comment"

@Component
class TitleGenerator(
    private val chatClient: ChatClient,
    @Value("classpath:prompts/title-prompt.st")
    private val titlePromptTemplate: Resource,
) {
    fun generateTitle(
        region: String,
        comment: String,
        image: MultipartFile,
    ): Title {
        val imageMedia = Media(MimeTypeUtils.IMAGE_JPEG, image.resource)
        val prompt =
            PromptTemplate(titlePromptTemplate).render(
                mapOf(
                    REGION to region,
                    COMMENT to comment,
                ),
            )
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
            .entity(Title::class.java) ?: throw AppException(ErrorType.SERVER_ERROR)
    }
}
