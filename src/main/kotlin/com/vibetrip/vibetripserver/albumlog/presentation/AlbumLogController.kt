package com.vibetrip.vibetripserver.albumlog.presentation

import com.vibetrip.vibetripserver.albumlog.business.AlbumLogService
import com.vibetrip.vibetripserver.albumlog.domain.EditAlbumLog
import com.vibetrip.vibetripserver.albumlog.presentation.dto.request.AlbumLogRegisterRequest
import com.vibetrip.vibetripserver.albumlog.presentation.dto.request.AlbumLogUpdateRequest
import com.vibetrip.vibetripserver.albumlog.presentation.dto.response.AlbumLogListResponse
import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.support.paging.CursorDefault
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.response.ApiResponse
import com.vibetrip.vibetripserver.support.response.PageResponse
import com.vibetrip.vibetripserver.support.security.annotation.AuthMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@Tag(name = "AlbumLog", description = "앨범 로그 관련 API")
@RestController
@RequestMapping("/api/v1/albums/{albumId}/album-logs")
class AlbumLogController(
    private val albumLogService: AlbumLogService,
) {
    @Operation(summary = "앨범 로그 등록", description = "앨범에 새로운 로그를 등록합니다. 최대 5개의 이미지를 함께 업로드할 수 있습니다.")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun registerAlbumLog(
        @PathVariable albumId: Long,
        @Valid @RequestPart request: AlbumLogRegisterRequest,
        @Valid @Size(max = 5) @RequestPart(required = false) images: List<MultipartFile>?,
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<Unit>> {
        val albumLogId =
            albumLogService.registerAlbumLog(request.toNewAlbumLog(albumId, images), member.memberKey)

        return ResponseEntity
            .created(URI.create("/api/v1/albums/$albumId/album-logs/$albumLogId"))
            .body(ApiResponse.success())
    }

    @Operation(summary = "앨범 로그 목록 조회", description = "앨범의 로그 목록을 커서 기반 페이지네이션으로 조회합니다.")
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

    @Operation(summary = "앨범 로그 수정", description = "앨범 로그를 수정합니다. 새 이미지 추가(최대 5개), 기존 이미지 삭제가 가능합니다.")
    @PutMapping(
        "/{albumLogId}",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updateAlbumLog(
        @PathVariable albumId: Long,
        @PathVariable albumLogId: Long,
        @Valid @RequestPart request: AlbumLogUpdateRequest,
        @Valid @Size(max = 5) @RequestPart(required = false) newImages: List<MultipartFile>?,
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<Unit>> {
        albumLogService.updateAlbumLog(
            EditAlbumLog.of(
                id = albumLogId,
                description = request.description,
                albumId = albumId,
                newImages = newImages,
                removeImageIds = request.removeImageIds,
            ),
            member.memberKey,
        )

        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "앨범 로그 삭제", description = "앨범 로그를 삭제합니다. 해당 로그에 포함된 이미지도 함께 삭제됩니다.")
    @DeleteMapping("/{albumLogId}")
    fun deleteAlbumLog(
        @PathVariable albumId: Long,
        @PathVariable albumLogId: Long,
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<Unit>> {
        albumLogService.deleteAlbumLog(albumId, albumLogId, member.memberKey)

        return ResponseEntity.ok(ApiResponse.success())
    }
}
