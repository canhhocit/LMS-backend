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
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<QuizResponse> createQuiz(
            @PathVariable Long classId,
            @Valid @RequestBody QuizRequest request) {
        return ApiResponse.success(quizService.createQuiz(classId, request));
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
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<QuizResponse> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequest request) {
        return ApiResponse.success(quizService.updateQuiz(quizId, request));
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ApiResponse.success(null);
    }

    // ==================== Question CRUD ====================

    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<QuestionResponse> createQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.success(quizService.createQuestion(quizId, request));
    }

    @GetMapping("/{quizId}/questions")
    public ApiResponse<List<QuestionResponse>> getQuestionsByQuiz(@PathVariable Long quizId) {
        return ApiResponse.success(quizService.getQuestionsByQuizId(quizId));
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequest request) {
        return ApiResponse.success(quizService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return ApiResponse.success(null);
    }

    // ==================== Quiz Attempt ====================

    @PostMapping("/{quizId}/attempts")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizAttemptResponse> submitAttempt(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizAttemptRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.submitAttempt(quizId, userPrincipal.getUser().getId(), request));
    }
}
