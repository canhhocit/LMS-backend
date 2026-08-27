package com.ex.learninghub.modules.grading.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.dto.request.AttendanceRequest;
import com.ex.learninghub.modules.grading.dto.request.GradeRequest;
import com.ex.learninghub.modules.grading.dto.response.AttendanceResponse;
import com.ex.learninghub.modules.grading.dto.response.GradeResponse;
import com.ex.learninghub.modules.grading.dto.response.TranscriptResponse;
import com.ex.learninghub.modules.grading.service.GradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Điểm số & Điểm danh", description = "Quản lý điểm, điểm danh và bảng điểm tổng hợp (transcript)")
public class GradingController {

    private final GradingService gradingService;

    // Grades

    @PostMapping("/classes/{classId}/grades")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(summary = "Nhập/Cập nhật điểm cho sinh viên", description = "Giảng viên nhập điểm hoặc cập nhật điểm cho sinh viên trong lớp học phần mà mình phụ trách.")
    public ApiResponse<GradeResponse> upsertGrade(@PathVariable Long classId,
                                                    @Valid @RequestBody GradeRequest request,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.upsertGrade(classId, request, userPrincipal));
    }

    @GetMapping("/classes/{classId}/grades")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    @Operation(summary = "Lấy bảng điểm của lớp học phần", description = "Giảng viên/Admin xem toàn bộ điểm của các sinh viên trong một lớp học phần.")
    public ApiResponse<List<GradeResponse>> getGradesByClass(@PathVariable Long classId,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getGradesByClass(classId, userPrincipal));
    }

    @GetMapping("/me/grades")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Sinh viên xem điểm của tôi", description = "Trả về tất cả các đầu điểm của sinh viên hiện tại theo từng lớp học phần.")
    public ApiResponse<List<GradeResponse>> getMyGrades(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getMyGrades(userPrincipal));
    }

    // ─── Attendance ──────────────────────────────────────────────────────────────

    @PostMapping("/classes/{classId}/attendance")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(summary = "Lưu điểm danh cho lớp", description = "Giảng viên gửi danh sách điểm danh (present/absent/late) cho các sinh viên trong lớp theo một buổi học.")
    public ApiResponse<List<AttendanceResponse>> saveAttendance(@PathVariable Long classId,
                                                                  @Valid @RequestBody AttendanceRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.saveAttendance(classId, request, userPrincipal));
    }

    @GetMapping("/classes/{classId}/attendance")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(summary = "Lấy điểm danh theo ngày", description = "Giảng viên xem danh sách điểm danh của lớp theo một ngày cụ thể.")
    public ApiResponse<List<AttendanceResponse>> getAttendanceByDate(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getAttendanceByDate(classId, date, userPrincipal));
    }

    @GetMapping("/classes/{classId}/attendance/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Sinh viên xem điểm danh của tôi trong lớp", description = "Sinh viên xem lịch sử điểm danh của mình trong một lớp học phần.")
    public ApiResponse<List<AttendanceResponse>> getMyAttendance(@PathVariable Long classId,
                                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getMyAttendance(classId, userPrincipal));
    }

    @GetMapping("/me/transcript")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Sinh viên xem bảng điểm tổng hợp", description = "Trả về bảng điểm tổng hợp (transcript) qua tất cả các học kỳ của sinh viên.")
    public ApiResponse<List<TranscriptResponse>> getMyTranscript(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(gradingService.getMyTranscript(userPrincipal));
    }
}
