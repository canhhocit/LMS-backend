package com.ex.learninghub.modules.grading.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.dto.response.AcademicStatusResponse;
import com.ex.learninghub.modules.grading.service.AcademicStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AcademicStatusController {

    private final AcademicStatusService academicStatusService;

    @GetMapping("/me/academic-status")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AcademicStatusResponse>> getMyStatus(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                academicStatusService.getMyAcademicStatus(principal)));
    }

    /**
     * Quét tất cả SV có GPA thấp → gửi Notification cảnh báo.
     * Gọi thủ công hoặc từ cron scheduler.
     */
    @PostMapping("/admin/academic-probation/scan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> scanProbation() {
        int warned = academicStatusService.scanAndWarnAcademicProbation();
        return ResponseEntity.ok(ApiResponse.success(warned));
    }
}
