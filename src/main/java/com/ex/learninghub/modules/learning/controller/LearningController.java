package com.ex.learninghub.modules.learning.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.learning.dto.response.EnrollmentResponse;
import com.ex.learninghub.modules.learning.dto.response.ProgressResponse;
import com.ex.learninghub.modules.learning.service.LearningService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
@Tag(name = "Learning Module", description = "Endpoints for Enrollments and Progress tracking")
public class LearningController {

    private final LearningService learningService;

    @PostMapping("/enroll/{courseId}")
    @Operation(summary = "Enroll in a course")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        EnrollmentResponse response = learningService.enrollCourse(courseId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<EnrollmentResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Enrolled successfully")
                        .result(response)
                        .build()
        );
    }

    @GetMapping("/my-enrollments")
    @Operation(summary = "Get current user's course enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<EnrollmentResponse> response = learningService.getMyEnrollments(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.<List<EnrollmentResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Enrollments retrieved successfully")
                        .result(response)
                        .build()
        );
    }

    @PutMapping("/enrollments/{enrollmentId}/lessons/{lessonId}")
    @Operation(summary = "Update lesson completion progress")
    public ResponseEntity<ApiResponse<ProgressResponse>> updateProgress(
            @PathVariable Long enrollmentId,
            @PathVariable Long lessonId,
            @RequestParam boolean isCompleted) {
        ProgressResponse response = learningService.updateProgress(enrollmentId, lessonId, isCompleted);
        return ResponseEntity.ok(
                ApiResponse.<ProgressResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Progress updated successfully")
                        .result(response)
                        .build()
        );
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    @Operation(summary = "Get enrollment progress details")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getProgress(@PathVariable Long enrollmentId) {
        List<ProgressResponse> response = learningService.getProgress(enrollmentId);
        return ResponseEntity.ok(
                ApiResponse.<List<ProgressResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Progress retrieved successfully")
                        .result(response)
                        .build()
        );
    }
}
