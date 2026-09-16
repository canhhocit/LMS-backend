package com.ex.learninghub.modules.quiz.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.quiz.dto.request.AiGenerateQuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuestionResponse;
import com.ex.learninghub.modules.quiz.service.AiQuestionGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@Tag(name = "AI Quiz Generator", description = "Các API sinh câu hỏi trắc nghiệm tự động bằng AI từ bài giảng/văn bản")
public class AiQuizController {

    private final AiQuestionGeneratorService aiQuestionGeneratorService;

    @PostMapping("/generate-ai")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(
            summary = "Tự động trích xuất & sinh câu hỏi trắc nghiệm bằng AI",
            description = "Đọc nội dung bài giảng/văn bản và dùng AI tạo danh sách câu hỏi trắc nghiệm mẫu."
    )
    public ApiResponse<List<QuestionRequest>> generateQuestionsFromText(
            @Valid @RequestBody AiGenerateQuestionRequest request) {
        return ApiResponse.success(aiQuestionGeneratorService.generateQuestionsFromText(request));
    }

    @PostMapping("/{quizId}/generate-ai")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(
            summary = "Tự động sinh và gán câu hỏi AI trực tiếp vào Quiz",
            description = "Sinh danh sách câu hỏi bằng AI và lưu trực tiếp vào đề thi trắc nghiệm (Quiz)."
    )
    public ApiResponse<List<QuestionResponse>> generateAndAttachToQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody AiGenerateQuestionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(aiQuestionGeneratorService.generateAndAttachToQuiz(quizId, request, userPrincipal));
    }
}
