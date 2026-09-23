package com.ex.learninghub.modules.assessment.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.dto.response.AiGradingResponse;
import com.ex.learninghub.modules.assessment.dto.response.SubmissionResponse;
import com.ex.learninghub.modules.assessment.service.AiGradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assessments/submissions")
@RequiredArgsConstructor
@Tag(name = "AI Assessment Grading", description = "API AI hỗ trợ chấm bài tự luận và nhận xét tự động dành cho Giảng viên")
@SecurityRequirement(name = "bearerAuth")
public class AiGradingController {

    private final AiGradingService aiGradingService;

    @PostMapping("/{submissionId}/ai-evaluate")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(summary = "Yêu cầu AI phân tích và đề xuất điểm/nhận xét cho bài nộp tự luận")
    public ResponseEntity<ApiResponse<AiGradingResponse>> evaluateSubmission(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        AiGradingResponse response = aiGradingService.evaluateSubmission(submissionId, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{submissionId}/ai-apply-grade")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(summary = "Áp dụng kết quả điểm & nhận xét của AI trực tiếp vào bài nộp của sinh viên")
    public ResponseEntity<ApiResponse<SubmissionResponse>> applyAiGrade(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SubmissionResponse response = aiGradingService.applyAiGrade(submissionId, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
