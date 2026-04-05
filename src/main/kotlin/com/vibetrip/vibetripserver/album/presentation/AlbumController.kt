package com.vibetrip.vibetripserver.album.presentation

import com.vibetrip.vibetripserver.album.business.AlbumService
import com.vibetrip.vibetripserver.album.presentation.dto.request.AlbumCreateRequest
import com.vibetrip.vibetripserver.album.presentation.dto.request.AlbumUpdateRequest
import com.vibetrip.vibetripserver.album.presentation.dto.request.SunoCallbackRequest
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumCreateResponse
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumDetailResponse
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumListResponse
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumPageResponse
import com.vibetrip.vibetripserver.album.presentation.dto.response.PreviewLogImage
import com.vibetrip.vibetripserver.albumlog.business.AlbumLogService
import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.support.paging.CursorDefault
import com.vibetrip.vibetripserver.support.paging.Cursorable
import com.vibetrip.vibetripserver.support.response.ApiResponse
import com.vibetrip.vibetripserver.support.security.annotation.AuthMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Album", description = "앨범 관련 API")
@RestController
@RequestMapping("/api/v1/albums")
class AlbumController(
    private val albumService: AlbumService,
    private val albumLogService: AlbumLogService,
) {
    @Operation(summary = "앨범생성", description = "새로운 앨범을 생성합니다.")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createAlbum(
        @AuthMember member: Member,
        @RequestPart coverImage: MultipartFile,
        @Valid @RequestPart request: AlbumCreateRequest,
    ): ResponseEntity<ApiResponse<AlbumCreateResponse>> {
        val albumId = albumService.createAlbum(request.toNewAlbum(member.memberKey), coverImage)

        return ResponseEntity.ok(ApiResponse.success(AlbumCreateResponse(albumId)))
    }

    @Operation(summary = "앨범 목록 조회", description = "앨범 목록을 조회합니다")
    @GetMapping
    fun getAlbums(
        @AuthMember member: Member,
        @CursorDefault cursorable: Cursorable<Long>,
    ): ResponseEntity<ApiResponse<AlbumPageResponse>> {
        val totalCount = albumService.countAlbums(member.memberKey)
        val slice =
            albumService
                .findAlbums(member.memberKey, cursorable)
                .map {
                    AlbumListResponse.from(
                        album = it,
                        previewLogImages =
                            albumLogService
                                .findAlbumLogImages(it.albumId, 3L)
                                .map { image -> PreviewLogImage(image.imageUrl) },
                        logImageCount = albumLogService.findAlbumLogCount(it.albumId),
                    )
                }

        return ResponseEntity.ok(ApiResponse.success(AlbumPageResponse.of(totalCount, slice)))
    }

    @Operation(summary = "앨범 수정", description = "앨범을 수정합니다")
    @PutMapping(
        "/{albumId}",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updateAlbum(
        @PathVariable albumId: Long,
        @RequestPart(required = true) coverImage: MultipartFile,
        @Valid @RequestPart request: AlbumUpdateRequest,
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<Unit>> {
        albumService.updateAlbum(
            albumId,
            request.toNewAlbum(member.memberKey),
            coverImage,
        )
        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "단일 앨범 조회", description = "단일 앨범을 조회합니다")
    @GetMapping("/{albumId}")
    fun getAlbum(
        @PathVariable albumId: Long,
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<AlbumDetailResponse>> =
        ResponseEntity.ok(
            ApiResponse.success(
                AlbumDetailResponse.from(albumService.findAlbum(albumId, member.memberKey)),
            ),
        )

    @Operation(summary = "앨범 삭제", description = "해당 앨범을 삭제합니다")
    @DeleteMapping("/{albumId}")
    fun deleteAlbum(
        @PathVariable albumId: Long,
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<Unit>> {
        albumService.deleteAlbum(albumId, member.memberKey)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @PostMapping("/suno/callback")
    fun sunoCallback(
        @RequestBody request: SunoCallbackRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        albumService.updateMusic(request.toSunoMusicData())

        return ResponseEntity.ok(ApiResponse.success())
    }
}
