package com.vibetrip.vibetripserver.config.api

import com.vibetrip.vibetripserver.support.paging.CursorableArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val cursorableArgumentResolver: CursorableArgumentResolver,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(cursorableArgumentResolver)
    }
}
