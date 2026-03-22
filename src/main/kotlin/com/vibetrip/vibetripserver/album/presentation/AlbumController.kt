package com.vibetrip.vibetripserver.album.presentation

import com.vibetrip.vibetripserver.album.business.AlbumService
import com.vibetrip.vibetripserver.album.presentation.dto.request.AlbumCreateRequest
import com.vibetrip.vibetripserver.album.presentation.dto.response.AlbumCreateResponse
import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.support.response.ApiResponse
import com.vibetrip.vibetripserver.support.security.annotation.AuthMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
        @RequestPart image: MultipartFile,
        @Valid @RequestPart request: AlbumCreateRequest,
    ): ResponseEntity<ApiResponse<AlbumCreateResponse>> {
        return ResponseEntity.ok(ApiResponse.success(albumService.create(request.toNewAlbum(member.memberKey), image)))
    }
}
