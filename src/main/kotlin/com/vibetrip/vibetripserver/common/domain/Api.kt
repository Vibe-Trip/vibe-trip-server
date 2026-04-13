package com.vibetrip.vibetripserver.common.domain

import org.springframework.http.HttpMethod

data class Api(
    val url: String,
    val method: HttpMethod? = null,
) {
    companion object {
        fun get(url: String) = Api(url, HttpMethod.GET)

        fun post(url: String) = Api(url, HttpMethod.POST)

        fun put(url: String) = Api(url, HttpMethod.PUT)

        fun patch(url: String) = Api(url, HttpMethod.PATCH)

        fun delete(url: String) = Api(url, HttpMethod.DELETE)
    }

    fun matchesMethod(requestMethod: String): Boolean = method == null || method.matches(requestMethod)
}

fun String.permitAll() = Api(this)
