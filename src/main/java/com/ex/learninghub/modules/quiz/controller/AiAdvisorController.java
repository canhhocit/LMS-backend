package com.ex.learninghub.modules.quiz.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.quiz.dto.request.AiAdvisorRequest;
import com.ex.learninghub.modules.quiz.dto.response.AiAdvisorResponse;
import com.ex.learninghub.modules.quiz.service.AiAdvisorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/advisor")
@RequiredArgsConstructor
@Tag(name = "AI Academic Advisor", description = "Các API Cố vấn Học tập AI - Phân tích phong cách học tập & đề xuất lộ trình cải thiện điểm số")
public class AiAdvisorController {

    private final AiAdvisorService aiAdvisorService;

    @GetMapping("/my-analysis")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Phân tích phong cách học tập & đề xuất cho sinh viên hiện tại",
            description = "Tự động tổng hợp điểm số, tính toán nhãn trạng thái và sinh lộ trình cải thiện học tập cá nhân hóa."
    )
    public ApiResponse<AiAdvisorResponse> getMyAnalysis(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(aiAdvisorService.analyzeCurrentStudent(principal));
    }

    @PostMapping("/analyze-student")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(
            summary = "Phân tích cố vấn học tập cho sinh viên chỉ định (Dành cho Giảng viên/Admin)",
            description = "Giảng viên chọn sinh viên bất kỳ để hệ thống AI cố vấn đưa ra báo cáo phân tích và lộ trình đề xuất."
    )
    public ApiResponse<AiAdvisorResponse> analyzeStudent(
            @RequestParam Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(aiAdvisorService.analyzeStudentById(studentId, principal));
    }

    @PostMapping("/ask")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'ADMIN')")
    @Operation(
            summary = "Đặt câu hỏi tham vấn trực tiếp với AI Advisor",
            description = "Sinh viên/Giảng viên nhập thắc mắc cụ thể về môn học để nhận định hướng giải đáp từ Cố vấn AI."
    )
    public ApiResponse<AiAdvisorResponse> askAdvisor(
            @RequestBody AiAdvisorRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(aiAdvisorService.askAdvisor(request, principal));
    }
}
