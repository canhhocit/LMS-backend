package com.ex.learninghub.modules.schedule.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.schedule.dto.request.ClassScheduleRequest;
import com.ex.learninghub.modules.schedule.dto.response.ScheduleResponse;
import com.ex.learninghub.modules.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Lịch học", description = "Các API quản lý và xem lịch học (thời khóa biểu) của lớp học phần và cá nhân")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/clazzes/{clazzId}/schedules")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    @Operation(
            summary = "Tạo buổi học trong lịch",
            description = "Giảng viên/Admin tạo một buổi học mới trong lịch của lớp học phần (gắn với ngày, phòng, tiết học)."
    )
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @Parameter(description = "ID của lớp học phần", example = "1")
            @PathVariable Long clazzId,
            @Valid @RequestBody ClassScheduleRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.createSchedule(clazzId, request, principal)));
    }

    @PutMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    @Operation(
            summary = "Cập nhật buổi học trong lịch",
            description = "Giảng viên/Admin cập nhật thông tin buổi học (ngày, phòng, giờ, ...)."
    )
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @Parameter(description = "ID của buổi học", example = "1")
            @PathVariable Long scheduleId,
            @Valid @RequestBody ClassScheduleRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.updateSchedule(scheduleId, request, principal)));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    @Operation(
            summary = "Xóa buổi học trong lịch",
            description = "Xóa một buổi học khỏi lịch của lớp học phần."
    )
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @Parameter(description = "ID của buổi học cần xóa", example = "1")
            @PathVariable Long scheduleId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        scheduleService.deleteSchedule(scheduleId, principal);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/clazzes/{clazzId}/schedules")
    @Operation(
            summary = "Lấy lịch học của lớp học phần",
            description = "Trả về toàn bộ lịch học (các buổi) của một lớp học phần."
    )
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedulesByClazz(
            @Parameter(description = "ID của lớp học phần", example = "1")
            @PathVariable Long clazzId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.getSchedulesByClazz(clazzId, principal)));
    }

    @GetMapping("/me/schedule")
    @Operation(
            summary = "Lấy lịch học của tôi trong tuần",
            description = "Người dùng hiện tại xem thời khóa biểu (TKB) trong tuần của mình."
    )
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getMySchedule(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.getMyWeeklySchedule(principal)));
    }
}
