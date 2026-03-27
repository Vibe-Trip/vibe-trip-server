package com.vibetrip.vibetripserver.album.presentation.dto.response

import com.vibetrip.vibetripserver.support.paging.Slice

data class AlbumPageResponse(
    val totalCount: Long,
    val content: List<AlbumListResponse>,
    val hasNext: Boolean,
) {
    companion object {
        fun of(
            totalCount: Long,
            slice: Slice<AlbumListResponse>,
        ) = AlbumPageResponse(
            totalCount = totalCount,
            content = slice.content,
            hasNext = slice.hasNext,
        )
    }
}
