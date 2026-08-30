package com.ex.learninghub.modules.content.controller;

import com.ex.learninghub.modules.content.entity.InVideoQuiz;
import com.ex.learninghub.modules.content.entity.StudentVideoNote;
import com.ex.learninghub.modules.content.entity.VideoProgress;
import com.ex.learninghub.modules.content.service.VideoLearningService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/video-learning")
@RequiredArgsConstructor
public class VideoLearningController {

    private final VideoLearningService videoLearningService;

    /**
     * Update or insert video progress for a student.
     * Request body contains enrollmentId, lessonId, lastWatchedSeconds, maxWatchedSeconds.
     */
    @PostMapping("/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<VideoProgress> upsertProgress(@RequestBody VideoProgressDto dto) {
        VideoProgress vp = videoLearningService.upsertProgress(
                dto.getEnrollmentId(),
                dto.getLessonId(),
                dto.getLastWatchedSeconds(),
                dto.getMaxWatchedSeconds()
        );
        return ResponseEntity.ok(vp);
    }

    @GetMapping("/progress/lesson/{lessonId}/enrollment/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<VideoProgress> getProgress(@PathVariable Long lessonId, @PathVariable Long enrollmentId) {
        return videoLearningService.getProgress(enrollmentId, lessonId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/quizzes/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('STUDENT','LECTURER','ADMIN')")
    public ResponseEntity<List<InVideoQuiz>> getQuizzes(@PathVariable Long lessonId) {
        List<InVideoQuiz> quizzes = videoLearningService.getQuizzesForLesson(lessonId);
        return ResponseEntity.ok(quizzes);
    }

    @PostMapping("/quizzes")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public ResponseEntity<InVideoQuiz> createQuiz(@RequestBody InVideoQuiz quiz) {
        InVideoQuiz saved = videoLearningService.createQuiz(quiz);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/notes/lesson/{lessonId}/user/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<StudentVideoNote>> getNotes(@PathVariable Long lessonId, @PathVariable Long userId) {
        List<StudentVideoNote> notes = videoLearningService.getNotes(userId, lessonId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/notes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentVideoNote> addNote(@RequestBody StudentVideoNoteDto dto) {
        StudentVideoNote note = videoLearningService.addNote(
                dto.getUserId(),
                dto.getLessonId(),
                dto.getNoteText(),
                dto.getTimestampSeconds()
        );
        return ResponseEntity.ok(note);
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
        private Long userId;
        private Long lessonId;
        private String noteText;
        private BigDecimal timestampSeconds;
    }
}
