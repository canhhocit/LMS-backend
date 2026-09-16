package com.ex.learninghub.modules.grading.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.grading.dto.response.AcademicRiskResponse;
import com.ex.learninghub.modules.grading.service.AcademicRiskWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grading/risk-warning")
@RequiredArgsConstructor
@Tag(name = "Dự báo Nguy cơ Cấm thi & Trượt môn", description = "Các API phân tích và dự báo sớm nguy cơ rủi ro học tập cho sinh viên")
public class AcademicRiskController {

    private final AcademicRiskWarningService academicRiskWarningService;

    @GetMapping("/class/{classId}/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT')")
    @Operation(
            summary = "Tính toán nguy cơ rủi ro học tập cá nhân của sinh viên",
            description = "Đánh giá tỷ lệ vắng mặt và điểm số để xếp loại mức độ rủi ro (SAFE, WARNING, CRITICAL)."
    )
    public ApiResponse<AcademicRiskResponse> calculateStudentRisk(
            @PathVariable Long classId,
            @PathVariable Long studentId) {
        return ApiResponse.success(academicRiskWarningService.calculateStudentRisk(classId, studentId));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(
            summary = "Lấy báo cáo tổng hợp danh sách sinh viên có nguy cơ rủi ro trong lớp",
            description = "Giảng viên / Cố vấn học tập xem danh sách sinh viên có nguy cơ bị cấm thi hoặc học lại."
    )
    public ApiResponse<List<AcademicRiskResponse>> getClassRiskReport(@PathVariable Long classId) {
        return ApiResponse.success(academicRiskWarningService.getClassRiskReport(classId));
    }
}
