package com.ex.learninghub.modules.quiz.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuestionResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizAttemptResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizResponse;
import com.ex.learninghub.modules.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Quiz", description = "Quản lý Quiz (bài kiểm tra trắc nghiệm), câu hỏi và lượt làm bài")
public class QuizController {

    private final QuizService quizService;

    // ==================== Quiz CRUD ====================

    @PostMapping("/class/{classId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    @Operation(summary = "Tạo Quiz mới cho lớp học phần", description = "Giảng viên/Admin tạo một Quiz (bài kiểm tra trắc nghiệm) mới cho lớp học phần.")
    public ApiResponse<QuizResponse> createQuiz(
            @PathVariable Long classId,
            @Valid @RequestBody QuizRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.createQuiz(classId, request, userPrincipal));
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "Lấy chi tiết Quiz", description = "Trả về thông tin chi tiết của một Quiz.")
    public ApiResponse<QuizResponse> getQuiz(@PathVariable Long quizId) {
        return ApiResponse.success(quizService.getQuizById(quizId));
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Lấy danh sách Quiz của lớp", description = "Trả về danh sách các Quiz thuộc về một lớp học phần.")
    public ApiResponse<List<QuizResponse>> getQuizzesByClass(@PathVariable Long classId) {
        return ApiResponse.success(quizService.getQuizzesByClassId(classId));
    }

    @PutMapping("/{quizId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    @Operation(summary = "Cập nhật Quiz", description = "Cập nhật thông tin Quiz (thời lượng, thời gian mở/đóng, ...).")
    public ApiResponse<QuizResponse> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.updateQuiz(quizId, request, userPrincipal));
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    @Operation(summary = "Xóa Quiz", description = "Xóa một Quiz cùng toàn bộ câu hỏi và lượt làm bài liên quan.")
    public ApiResponse<Void> deleteQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        quizService.deleteQuiz(quizId, userPrincipal);
        return ApiResponse.success(null);
    }

    // ==================== Question CRUD ====================

    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    @Operation(summary = "Tạo câu hỏi cho Quiz", description = "Giảng viên/Admin thêm một câu hỏi (kèm đáp án) vào một Quiz đã có.")
    public ApiResponse<QuestionResponse> createQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.createQuestion(quizId, request, userPrincipal));
    }

    @GetMapping("/{quizId}/questions")
    @Operation(summary = "Lấy danh sách câu hỏi của Quiz", description = "Trả về danh sách câu hỏi của một Quiz.")
    public ApiResponse<List<QuestionResponse>> getQuestionsByQuiz(@PathVariable Long quizId) {
        return ApiResponse.success(quizService.getQuestionsByQuizId(quizId));
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    @Operation(summary = "Cập nhật câu hỏi", description = "Giảng viên/Admin cập nhật nội dung hoặc đáp án của một câu hỏi.")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.updateQuestion(questionId, request, userPrincipal));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    @Operation(summary = "Xóa câu hỏi", description = "Giảng viên/Admin xóa một câu hỏi khỏi Quiz.")
    public ApiResponse<Void> deleteQuestion(
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        quizService.deleteQuestion(questionId, userPrincipal);
        return ApiResponse.success(null);
    }

    // ==================== Quiz Attempt ====================

    @PostMapping("/{quizId}/start")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Bắt đầu làm một Quiz", description = "Sinh viên bắt đầu một lượt làm Quiz (khởi tạo attempt). Trả về thời điểm bắt đầu.")
    public ApiResponse<java.time.LocalDateTime> startQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.startQuiz(quizId, userPrincipal));
    }

    @PostMapping("/{quizId}/attempts")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Nộp bài làm Quiz", description = "Sinh viên nộp đáp án cho một Quiz và nhận về kết quả (điểm và đáp án đúng).")
    public ApiResponse<QuizAttemptResponse> submitAttempt(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizAttemptRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(quizService.submitAttempt(quizId, userPrincipal.getUser().getId(), request));
    }
}
