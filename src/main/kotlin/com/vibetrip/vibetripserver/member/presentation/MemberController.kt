package com.vibetrip.vibetripserver.member.presentation

import com.vibetrip.vibetripserver.album.business.AlbumService
import com.vibetrip.vibetripserver.albumlog.business.AlbumLogService
import com.vibetrip.vibetripserver.member.business.MemberService
import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.member.presentation.dto.response.MemberResponse
import com.vibetrip.vibetripserver.support.response.ApiResponse
import com.vibetrip.vibetripserver.support.security.annotation.AuthMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Member", description = "멤버 관련 API")
@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService,
    private val albumService: AlbumService,
    private val albumLogService: AlbumLogService,
) {
    @Operation(summary = "프로필 조회", description = "멤버의 프로필을 조회합니다.")
    @GetMapping("/profile")
    fun getMemberProfile(
        @AuthMember authMember: Member,
    ): ResponseEntity<ApiResponse<MemberResponse>> {
        val member = memberService.getMember(authMember.memberKey)
        val albumCount = albumService.getAlbumCount(member.memberKey)
        val albumLogCount = albumLogService.getAlbumLogCount(member.memberKey)

        return ResponseEntity.ok(
            ApiResponse.success(
                MemberResponse(
                    name = member.nameValue,
                    email = member.emailValue,
                    profileImage = member.profileImageUrlValue,
                    albumCount = albumCount,
                    albumLogCount = albumLogCount,
                ),
            ),
        )
    }

    @DeleteMapping("/me/withdraw")
    fun withdraw(
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<Unit>> {
        memberService.withdraw(member.memberKey)

        return ResponseEntity.ok(ApiResponse.success())
    }
}
