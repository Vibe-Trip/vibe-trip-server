package com.vibetrip.vibetripserver.member.presentation

import com.vibetrip.vibetripserver.member.business.MemberService
import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.support.response.ApiResponse
import com.vibetrip.vibetripserver.support.security.annotation.AuthMember
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService,
) {
    @DeleteMapping("/me/withdraw")
    fun withdraw(
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<Unit>> {
        memberService.withdraw(member.memberKey)

        return ResponseEntity.ok(ApiResponse.success())
    }
}
