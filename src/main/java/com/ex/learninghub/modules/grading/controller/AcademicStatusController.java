package com.ex.learninghub.modules.grading.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.dto.response.AcademicStatusResponse;
import com.ex.learninghub.modules.grading.service.AcademicStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Tình trạng học tập", description = "Các API xem tình trạng học tập của sinh viên và cảnh báo học vụ")
public class AcademicStatusController {

    private final AcademicStatusService academicStatusService;

    @GetMapping("/me/academic-status")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Xem tình trạng học tập của tôi",
            description = "Sinh viên xem GPA, tổng tín chỉ, các môn đang học và các cảnh báo học vụ (nếu có)."
    )
    public ResponseEntity<ApiResponse<AcademicStatusResponse>> getMyStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                academicStatusService.getMyAcademicStatus(principal)));
    }

    /**
     * Quét tất cả SV có GPA thấp → gửi Notification cảnh báo.
     * Gọi thủ công hoặc từ cron scheduler.
     */
    @PostMapping("/admin/academic-probation/scan")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Quét sinh viên cảnh báo học vụ",
            description = "Quét tất cả sinh viên có GPA thấp và gửi Notification cảnh báo. Trả về số SV đã được cảnh báo."
    )
    public ResponseEntity<ApiResponse<Integer>> scanProbation() {
        int warned = academicStatusService.scanAndWarnAcademicProbation();
        return ResponseEntity.ok(ApiResponse.success(warned));
    }
}
