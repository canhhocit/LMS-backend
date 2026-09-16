package com.ex.learninghub.modules.content.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.entity.InVideoQuiz;
import com.ex.learninghub.modules.content.entity.StudentVideoNote;
import com.ex.learninghub.modules.content.entity.VideoProgress;
import com.ex.learninghub.modules.content.service.VideoLearningService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<VideoProgress> upsertProgress(
            @RequestBody VideoProgressDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        VideoProgress vp = videoLearningService.upsertProgress(
                dto.getEnrollmentId(),
                dto.getLessonId(),
                dto.getLastWatchedSeconds(),
                dto.getMaxWatchedSeconds(),
                userPrincipal
        );
        return ApiResponse.success(vp);
    }

    @GetMapping("/progress/lesson/{lessonId}/enrollment/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<VideoProgress> getProgress(
            @PathVariable Long lessonId,
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(
                videoLearningService.getProgress(enrollmentId, lessonId, userPrincipal).orElse(null)
        );
    }

    @GetMapping("/quizzes/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('STUDENT','LECTURER','ADMIN')")
    public ApiResponse<List<InVideoQuiz>> getQuizzes(@PathVariable Long lessonId) {
        List<InVideoQuiz> quizzes = videoLearningService.getQuizzesForLesson(lessonId);
        return ApiResponse.success(quizzes);
    }

    @PostMapping("/quizzes")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public ApiResponse<InVideoQuiz> createQuiz(
            @RequestBody InVideoQuiz quiz,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        InVideoQuiz saved = videoLearningService.createQuiz(quiz, userPrincipal);
        return ApiResponse.success(saved);
    }

    @GetMapping("/notes/lesson/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<StudentVideoNote>> getNotes(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<StudentVideoNote> notes = videoLearningService.getNotes(userPrincipal.getUser().getId(), lessonId);
        return ApiResponse.success(notes);
    }

    @PostMapping("/notes")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<StudentVideoNote> addNote(
            @RequestBody StudentVideoNoteDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudentVideoNote note = videoLearningService.addNote(
                userPrincipal.getUser().getId(),
                dto.getLessonId(),
                dto.getNoteText(),
                dto.getTimestampSeconds()
        );
        return ApiResponse.success(note);
    }

    // DTO classes for request payloads
    @Data
    public static class VideoProgressDto {
        private Long enrollmentId;
        private Long lessonId;
        private BigDecimal lastWatchedSeconds;
        private BigDecimal maxWatchedSeconds;
    }

    @Data
    public static class StudentVideoNoteDto {
        private Long lessonId;
        private String noteText;
        private BigDecimal timestampSeconds;
    }
}
