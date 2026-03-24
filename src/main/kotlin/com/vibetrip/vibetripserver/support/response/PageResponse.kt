package com.vibetrip.vibetripserver.support.response

import com.vibetrip.vibetripserver.support.paging.Slice

data class PageResponse<T>(
    val content: List<T>,
    val hasNext: Boolean,
) {
    companion object {
        fun <T> from(slice: Slice<T>) =
            PageResponse(
                content = slice.content,
                hasNext = slice.hasNext,
            )
    }
}
