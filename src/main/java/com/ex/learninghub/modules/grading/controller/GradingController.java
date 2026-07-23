package com.ex.learninghub.modules.grading.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.dto.request.AttendanceRequest;
import com.ex.learninghub.modules.grading.dto.request.GradeRequest;
import com.ex.learninghub.modules.grading.dto.response.AttendanceResponse;
import com.ex.learninghub.modules.grading.dto.response.GradeResponse;
import com.ex.learninghub.modules.grading.service.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GradingController {

    private final GradingService gradingService;

    // Grades 

    @PostMapping("/classes/{classId}/grades")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<GradeResponse> upsertGrade(@PathVariable Long classId,
                                                    @Valid @RequestBody GradeRequest request,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.upsertGrade(classId, request, userPrincipal));
    }

    @GetMapping("/classes/{classId}/grades")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public ApiResponse<List<GradeResponse>> getGradesByClass(@PathVariable Long classId,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getGradesByClass(classId, userPrincipal));
    }

    @GetMapping("/me/grades")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<GradeResponse>> getMyGrades(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getMyGrades(userPrincipal));
    }

    // ─── Attendance ──────────────────────────────────────────────────────────────

    @PostMapping("/classes/{classId}/attendance")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<List<AttendanceResponse>> saveAttendance(@PathVariable Long classId,
                                                                  @Valid @RequestBody AttendanceRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.saveAttendance(classId, request, userPrincipal));
    }

    @GetMapping("/classes/{classId}/attendance")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<List<AttendanceResponse>> getAttendanceByDate(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getAttendanceByDate(classId, date, userPrincipal));
    }

    @GetMapping("/classes/{classId}/attendance/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<AttendanceResponse>> getMyAttendance(@PathVariable Long classId,
                                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getMyAttendance(classId, userPrincipal));
    }
}
