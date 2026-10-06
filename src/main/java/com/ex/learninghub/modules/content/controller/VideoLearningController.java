package com.ex.learninghub.modules.content.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.request.InVideoQuizRequest;
import com.ex.learninghub.modules.content.dto.response.ManagedInVideoQuizResponse;
import com.ex.learninghub.modules.content.dto.response.StudentInVideoQuizResponse;
import com.ex.learninghub.modules.content.dto.response.VideoProgressResponse;
import com.ex.learninghub.modules.content.entity.StudentVideoNote;
import com.ex.learninghub.modules.content.service.VideoLearningService;
import com.ex.learninghub.common.enums.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/video-learning")
@RequiredArgsConstructor
public class VideoLearningController {

    private final VideoLearningService videoLearningService;

    /**
     * Update or insert video progress for a student.
     * Uses authenticated user ID instead of accepting it from request.
     */
    @PostMapping("/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<VideoProgressResponse> upsertProgress(
            @Valid @RequestBody VideoProgressDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        VideoProgressResponse progress = videoLearningService.upsertProgress(
                dto.getEnrollmentId(),
                dto.getLessonId(),
                dto.getLastWatchedSeconds(),
                dto.getMaxWatchedSeconds(),
                userPrincipal
        );
        return ApiResponse.success(progress);
    }

    @GetMapping("/progress/lesson/{lessonId}/enrollment/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<VideoProgressResponse> getProgress(
            @PathVariable Long lessonId,
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(
                videoLearningService.getProgress(enrollmentId, lessonId, userPrincipal).orElse(null)
        );
    }

    @GetMapping("/quizzes/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('STUDENT','LECTURER','ADMIN')")
    public ApiResponse<?> getQuizzes(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal.getUser().getRole() == Role.STUDENT) {
            List<StudentInVideoQuizResponse> quizzes = videoLearningService.getStudentQuizzes(lessonId, userPrincipal);
            return ApiResponse.success(quizzes);
        }
        List<ManagedInVideoQuizResponse> quizzes = videoLearningService.getManagedQuizzes(lessonId, userPrincipal);
        return ApiResponse.success(quizzes);
    }

    @PostMapping("/quizzes")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public ApiResponse<ManagedInVideoQuizResponse> createQuiz(
            @Valid @RequestBody InVideoQuizRequest quiz,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ManagedInVideoQuizResponse saved = videoLearningService.createQuiz(quiz, userPrincipal);
        return ApiResponse.success(saved);
    }

    @GetMapping("/notes/lesson/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<StudentVideoNote>> getNotes(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<StudentVideoNote> notes = videoLearningService.getNotes(lessonId, userPrincipal);
        return ApiResponse.success(notes);
    }

    @PostMapping("/notes")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<StudentVideoNote> addNote(
            @Valid @RequestBody StudentVideoNoteDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudentVideoNote note = videoLearningService.addNote(
                dto.getLessonId(),
                dto.getNoteText(),
                dto.getTimestampSeconds(),
                userPrincipal
        );
        return ApiResponse.success(note);
    }

    // DTO classes for request payloads
    @Data
    public static class VideoProgressDto {
        @NotNull
        private Long enrollmentId;
        @NotNull
        private Long lessonId;
        @NotNull
        @DecimalMin("0.0")
        private BigDecimal lastWatchedSeconds;
        @NotNull
        @DecimalMin("0.0")
        private BigDecimal maxWatchedSeconds;
    }

    @Data
    public static class StudentVideoNoteDto {
        @NotNull
        private Long lessonId;
        @jakarta.validation.constraints.NotBlank
        private String noteText;
        @NotNull
        @DecimalMin("0.0")
        private BigDecimal timestampSeconds;
    }
}
