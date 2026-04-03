package com.vibetrip.vibetripserver.alarm.presentation

import com.vibetrip.vibetripserver.alarm.business.AlarmService
import com.vibetrip.vibetripserver.alarm.presentation.dto.response.AlarmResponse
import com.vibetrip.vibetripserver.member.domain.Member
import com.vibetrip.vibetripserver.support.response.ApiResponse
import com.vibetrip.vibetripserver.support.security.annotation.AuthMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Alarm", description = "알림 관련 API")
@RestController
@RequestMapping("/api/v1/alarms")
class AlarmController(
    private val alarmService: AlarmService,
) {
    @Operation(summary = "알림 목록 조회")
    @GetMapping
    fun getAlarms(
        @AuthMember member: Member,
    ): ResponseEntity<ApiResponse<List<AlarmResponse>>> =
        ResponseEntity.ok(ApiResponse.success(alarmService.findAlarms(member.memberKey)))

    @Operation(summary = "알림 삭제")
    @DeleteMapping("/{alarmId}")
    fun deleteAlarm(
        @PathVariable alarmId: Long,
    ): ResponseEntity<ApiResponse<Unit>> {
        alarmService.deleteAlarm(alarmId)
        return ResponseEntity.ok(ApiResponse.success())
    }
}
