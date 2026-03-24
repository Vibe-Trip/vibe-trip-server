package com.vibetrip.vibetripserver.albumlog.presentation

import com.vibetrip.vibetripserver.albumlog.business.AlbumLogService
import com.vibetrip.vibetripserver.albumlog.presentation.dto.request.AlbumLogRegisterRequest
import com.vibetrip.vibetripserver.albumlog.presentation.dto.response.AlbumLogListResponse
import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.support.paging.CursorDefault
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.response.ApiResponse
import com.vibetrip.vibetripserver.support.response.PageResponse
import com.vibetrip.vibetripserver.support.security.annotation.AuthMember
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
@RequestMapping("/api/v1/albums/{albumId}/album-logs")
class AlbumLogController(
    private val albumLogService: AlbumLogService,
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun registerAlbumLog(
        @PathVariable albumId: Long,
        @Valid @RequestPart request: AlbumLogRegisterRequest,
        @Valid @Size(max = 5) @RequestPart(required = false) images: List<MultipartFile>,
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<Unit>> {
        val albumLogId = albumLogService.registerAlbumLog(request.toNewAlbumLog(albumId), images, member.memberKey)

        return ResponseEntity
            .created(URI.create("/api/v1/albums/$albumId/album-logs/$albumLogId"))
            .body(ApiResponse.success())
    }

    @GetMapping
    fun albumLogs(
        @PathVariable albumId: Long,
        @CursorDefault cursorable: Cursorable<Long>,
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<PageResponse<AlbumLogListResponse>>> {
        val slice =
            albumLogService
                .findAlbumLogs(albumId, cursorable, member.memberKey)
                .map(AlbumLogListResponse::from)

        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(slice)))
    }
}
