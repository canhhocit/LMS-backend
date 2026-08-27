package com.ex.learninghub.modules.enrollment.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.enrollment.dto.response.ProgressResponse;
import com.ex.learninghub.modules.enrollment.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Tiến độ học tập", description = "Các API sinh viên đánh dấu hoàn thành bài học và xem tiến độ")
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/progress/lessons/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Đánh dấu hoàn thành bài học",
            description = "Sinh viên đánh dấu một bài học là đã hoàn thành trong lớp học phần mà sinh viên đã đăng ký."
    )
    public ApiResponse<Void> markLessonCompleted(
            @Parameter(description = "ID của bài học", example = "1")
            @PathVariable Long lessonId,
            @Parameter(description = "ID của enrollment (ghi danh) của sinh viên", example = "1")
            @RequestParam Long enrollmentId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        progressService.markLessonCompleted(enrollmentId, lessonId, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Lấy tiến độ học tập của enrollment",
            description = "Trả về chi tiết tiến độ học tập (số bài học đã hoàn thành, tỷ lệ phần trăm, ...) cho một enrollment."
    )
    public ApiResponse<ProgressResponse> getProgress(
            @Parameter(description = "ID của enrollment", example = "1")
            @PathVariable Long enrollmentId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(progressService.getProgressByEnrollment(enrollmentId, userPrincipal));
    }
}
