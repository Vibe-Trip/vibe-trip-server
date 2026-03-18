package com.vibetrip.vibetripserver.auth.presentation

import com.vibetrip.vibetripserver.auth.business.OAuthService
import com.vibetrip.vibetripserver.auth.presentation.dto.request.AppleLoginRequest
import com.vibetrip.vibetripserver.auth.presentation.dto.request.KakaoLoginRequest
import com.vibetrip.vibetripserver.auth.presentation.dto.response.JwtResponse
import com.vibetrip.vibetripserver.common.util.getClientIp
import com.vibetrip.vibetripserver.support.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val oAuthService: OAuthService,
) {

    @PostMapping("/login/kakao")
    fun kakaoLogin(
        @Valid @RequestBody kakaoLoginRequest: KakaoLoginRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<JwtResponse>> {
        val jwt = oAuthService.login(kakaoLoginRequest.toNewOAuthLogin(getClientIp(httpRequest)))
        return ResponseEntity.ok(ApiResponse.success(JwtResponse(jwt.accessToken, jwt.refreshToken)))
    }

    @PostMapping("/login/apple")
    fun appleLogin(
        @Valid @RequestBody appleLoginRequest: AppleLoginRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<JwtResponse>> {
        val jwt = oAuthService.login(appleLoginRequest.toNewOAuthLogin(getClientIp(httpRequest)))
        return ResponseEntity.ok(ApiResponse.success(JwtResponse(jwt.accessToken, jwt.refreshToken)))
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestHeader(AUTHORIZATION) refreshToken: String
    ): ResponseEntity<ApiResponse<JwtResponse>> {
        val jwt = oAuthService.refresh(refreshToken)
        return ResponseEntity.ok(ApiResponse.success(JwtResponse(jwt.accessToken, jwt.refreshToken)))
    }
}