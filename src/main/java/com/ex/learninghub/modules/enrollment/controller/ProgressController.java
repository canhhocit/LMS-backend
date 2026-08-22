package com.ex.learninghub.modules.enrollment.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.enrollment.dto.response.ProgressResponse;
import com.ex.learninghub.modules.enrollment.service.ProgressService;
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
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/progress/lessons/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> markLessonCompleted(
            @PathVariable Long lessonId,
            @RequestParam Long enrollmentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        progressService.markLessonCompleted(enrollmentId, lessonId, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ProgressResponse> getProgress(
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(progressService.getProgressByEnrollment(enrollmentId, userPrincipal));
    }
}
