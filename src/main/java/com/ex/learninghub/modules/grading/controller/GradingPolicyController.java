package com.ex.learninghub.modules.grading.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.grading.dto.request.GpaScaleRuleRequest;
import com.ex.learninghub.modules.grading.dto.request.GradingPolicyRequest;
import com.ex.learninghub.modules.grading.dto.response.GpaScaleRuleResponse;
import com.ex.learninghub.modules.grading.dto.response.GradingPolicyResponse;
import com.ex.learninghub.modules.grading.service.GradingPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Chính sách điểm & GPA theo khóa", description = "Các API quản lý và tra cứu công thức tính điểm, thang quy đổi GPA theo khóa đào tạo")
public class GradingPolicyController {

    private final GradingPolicyService gradingPolicyService;

    // Public / Student endpoint to view grading policy of a curriculum
    @GetMapping("/curricula/{curriculumId}/grading-policy")
    @Operation(summary = "Xem công thức tính điểm của khóa đào tạo", description = "Sinh viên/Giảng viên xem trọng số tính điểm áp dụng cho khóa đào tạo.")
    public ResponseEntity<ApiResponse<GradingPolicyResponse>> getGradingPolicyPublic(@PathVariable Long curriculumId) {
        return ResponseEntity.ok(ApiResponse.success(gradingPolicyService.getGradingPolicy(curriculumId)));
    }

    // Admin endpoints
    @GetMapping("/admin/curricula/{curriculumId}/grading-policy")
    @PreAuthorize("hasRole('ADMIN') and @adminPermissionService.hasPermission(authentication, 'MANAGE_GRADING_POLICY')")
    @Operation(summary = "Admin lấy công thức tính điểm của khóa", description = "Lấy trọng số chuyên cần, giữa kỳ, cuối kỳ của khóa đào tạo.")
    public ResponseEntity<ApiResponse<GradingPolicyResponse>> getGradingPolicy(@PathVariable Long curriculumId) {
        return ResponseEntity.ok(ApiResponse.success(gradingPolicyService.getGradingPolicy(curriculumId)));
    }

    @PutMapping("/admin/curricula/{curriculumId}/grading-policy")
    @PreAuthorize("hasRole('ADMIN') and @adminPermissionService.hasPermission(authentication, 'MANAGE_GRADING_POLICY')")
    @Operation(summary = "Admin cập nhật công thức tính điểm của khóa", description = "Cập nhật trọng số chuyên cần, giữa kỳ, cuối kỳ (tổng phải bằng 1.0).")
    public ResponseEntity<ApiResponse<GradingPolicyResponse>> updateGradingPolicy(
            @PathVariable Long curriculumId,
            @Valid @RequestBody GradingPolicyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(gradingPolicyService.updateGradingPolicy(curriculumId, request)));
    }

    @GetMapping("/admin/curricula/{curriculumId}/gpa-scale")
    @PreAuthorize("hasRole('ADMIN') and @adminPermissionService.hasPermission(authentication, 'MANAGE_GRADING_POLICY')")
    @Operation(summary = "Admin lấy thang quy đổi GPA của khóa", description = "Lấy các quy tắc quy đổi điểm hệ 10 sang GPA hệ 4 của khóa đào tạo.")
    public ResponseEntity<ApiResponse<List<GpaScaleRuleResponse>>> getGpaScaleRules(@PathVariable Long curriculumId) {
        return ResponseEntity.ok(ApiResponse.success(gradingPolicyService.getGpaScaleRules(curriculumId)));
    }

    @PutMapping("/admin/curricula/{curriculumId}/gpa-scale")
    @PreAuthorize("hasRole('ADMIN') and @adminPermissionService.hasPermission(authentication, 'MANAGE_GRADING_POLICY')")
    @Operation(summary = "Admin cập nhật thang quy đổi GPA của khóa", description = "Thay thế toàn bộ quy tắc quy đổi GPA hệ 10 -> hệ 4 cho khóa đào tạo.")
    public ResponseEntity<ApiResponse<List<GpaScaleRuleResponse>>> updateGpaScaleRules(
            @PathVariable Long curriculumId,
            @Valid @RequestBody List<GpaScaleRuleRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(gradingPolicyService.updateGpaScaleRules(curriculumId, requests)));
    }
}
