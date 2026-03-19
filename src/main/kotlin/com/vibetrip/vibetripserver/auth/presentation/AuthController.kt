package com.vibetrip.vibetripserver.auth.presentation

import com.vibetrip.vibetripserver.auth.business.OAuthService
import com.vibetrip.vibetripserver.auth.presentation.dto.request.AppleLoginRequest
import com.vibetrip.vibetripserver.auth.presentation.dto.request.KakaoLoginRequest
import com.vibetrip.vibetripserver.auth.presentation.dto.response.JwtResponse
import com.vibetrip.vibetripserver.common.util.getClientIp
import com.vibetrip.vibetripserver.support.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val oAuthService: OAuthService,
) {

    @Operation(summary = "카카오 로그인", description = "카카오 OAuth 토큰으로 로그인합니다.")
    @PostMapping("/login/kakao")
    fun kakaoLogin(
        @Valid @RequestBody kakaoLoginRequest: KakaoLoginRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<JwtResponse>> {
        val jwt = oAuthService.login(kakaoLoginRequest.toNewOAuthLogin(getClientIp(httpRequest)))
        return ResponseEntity.ok(ApiResponse.success(JwtResponse(jwt.accessToken, jwt.refreshToken)))
    }

    @Operation(summary = "애플 로그인", description = "애플 Identity Token으로 로그인합니다.")
    @PostMapping("/login/apple")
    fun appleLogin(
        @Valid @RequestBody appleLoginRequest: AppleLoginRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<JwtResponse>> {
        val jwt = oAuthService.login(appleLoginRequest.toNewOAuthLogin(getClientIp(httpRequest)))
        return ResponseEntity.ok(ApiResponse.success(JwtResponse(jwt.accessToken, jwt.refreshToken)))
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token을 발급받습니다.")
    @PostMapping("/refresh")
    fun refreshToken(
        @Parameter(description = "Bearer {refreshToken}", required = true)
        @RequestHeader(AUTHORIZATION) refreshToken: String
    ): ResponseEntity<ApiResponse<JwtResponse>> {
        val jwt = oAuthService.refresh(refreshToken)
        return ResponseEntity.ok(ApiResponse.success(JwtResponse(jwt.accessToken, jwt.refreshToken)))
    }
}
