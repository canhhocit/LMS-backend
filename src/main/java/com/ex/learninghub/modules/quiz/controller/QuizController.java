package com.ex.learninghub.modules.quiz.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuestionResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizAttemptResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizResponse;
import com.ex.learninghub.modules.quiz.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ex.learninghub.common.security.UserPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // ==================== Quiz CRUD ====================

    @PostMapping("/class/{classId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public ApiResponse<QuizResponse> createQuiz(
            @PathVariable Long classId,
            @Valid @RequestBody QuizRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.createQuiz(classId, request, userPrincipal));
    }

    @GetMapping("/{quizId}")
    public ApiResponse<QuizResponse> getQuiz(@PathVariable Long quizId) {
        return ApiResponse.success(quizService.getQuizById(quizId));
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<List<QuizResponse>> getQuizzesByClass(@PathVariable Long classId) {
        return ApiResponse.success(quizService.getQuizzesByClassId(classId));
    }

    @PutMapping("/{quizId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public ApiResponse<QuizResponse> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.updateQuiz(quizId, request, userPrincipal));
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public ApiResponse<Void> deleteQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        quizService.deleteQuiz(quizId, userPrincipal);
        return ApiResponse.success(null);
    }

    // ==================== Question CRUD ====================

    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public ApiResponse<QuestionResponse> createQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.createQuestion(quizId, request, userPrincipal));
    }

    @GetMapping("/{quizId}/questions")
    public ApiResponse<List<QuestionResponse>> getQuestionsByQuiz(@PathVariable Long quizId) {
        return ApiResponse.success(quizService.getQuestionsByQuizId(quizId));
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.updateQuestion(questionId, request, userPrincipal));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public ApiResponse<Void> deleteQuestion(
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        quizService.deleteQuestion(questionId, userPrincipal);
        return ApiResponse.success(null);
    }

    // ==================== Quiz Attempt ====================

    @PostMapping("/{quizId}/start")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<java.time.LocalDateTime> startQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.startQuiz(quizId, userPrincipal));
    }

    @PostMapping("/{quizId}/attempts")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizAttemptResponse> submitAttempt(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizAttemptRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.submitAttempt(quizId, userPrincipal.getUser().getId(), request));
    }
}
