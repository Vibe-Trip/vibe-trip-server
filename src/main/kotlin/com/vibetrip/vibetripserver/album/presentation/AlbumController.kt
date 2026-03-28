package com.vibetrip.vibetripserver.album.presentation

import com.vibetrip.vibetripserver.album.business.AlbumService
import com.vibetrip.vibetripserver.album.presentation.dto.request.AlbumCreateRequest
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumCreateResponse
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumListResponse
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumPageResponse
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Album", description = "앨범 관련 API")
@RestController
@RequestMapping("/api/v1/albums")
class AlbumController(
    private val albumService: AlbumService,
) {
    @Operation(summary = "앨범생성", description = "새로운 앨범을 생성합니다.")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createAlbum(
        @AuthMember member: Member,
        @RequestPart coverImage: List<MultipartFile>,
        @Valid @RequestPart request: AlbumCreateRequest,
    ): ResponseEntity<ApiResponse<AlbumCreateResponse>> =
        ResponseEntity.ok(
            ApiResponse.success(
                albumService.createAlbum(
                    request.toNewAlbum(member.memberKey),
                    coverImage,
                ),
            ),
        )

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
                .map { AlbumListResponse.from(it) }

        return ResponseEntity.ok(ApiResponse.success(AlbumPageResponse.of(totalCount, slice)))
    }
}
