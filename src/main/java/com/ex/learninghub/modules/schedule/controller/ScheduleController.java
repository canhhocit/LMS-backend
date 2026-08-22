package com.ex.learninghub.modules.schedule.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.schedule.dto.request.ClassScheduleRequest;
import com.ex.learninghub.modules.schedule.dto.response.ScheduleResponse;
import com.ex.learninghub.modules.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/clazzes/{clazzId}/schedules")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @PathVariable Long clazzId,
            @Valid @RequestBody ClassScheduleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.createSchedule(clazzId, request, principal)));
    }

    @PutMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ClassScheduleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.updateSchedule(scheduleId, request, principal)));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal UserPrincipal principal) {
        scheduleService.deleteSchedule(scheduleId, principal);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/clazzes/{clazzId}/schedules")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedulesByClazz(
            @PathVariable Long clazzId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.getSchedulesByClazz(clazzId, principal)));
    }

    @GetMapping("/me/schedule")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getMySchedule(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.getMyWeeklySchedule(principal)));
    }
}
