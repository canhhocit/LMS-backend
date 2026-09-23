package com.ex.learninghub.modules.content.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.response.AiLessonSummaryResponse;
import com.ex.learninghub.modules.content.service.AiLessonSummarizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/content/lessons")
@RequiredArgsConstructor
@Tag(name = "AI Content Summarizer", description = "API AI tóm tắt nội dung bài học dành cho Sinh viên và Giảng viên")
@SecurityRequirement(name = "bearerAuth")
public class AiLessonSummarizerController {

    private final AiLessonSummarizerService aiLessonSummarizerService;

    @GetMapping("/{lessonId}/ai-summary")
    @Operation(summary = "Lấy bản tóm tắt nội dung bài học do AI phân tích")
    public ResponseEntity<ApiResponse<AiLessonSummaryResponse>> summarizeLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        AiLessonSummaryResponse response = aiLessonSummarizerService.summarizeLesson(lessonId, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
