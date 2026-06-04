package com.ex.learninghub.modules.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.admin.dto.request.MentorApprovalRequest;
import com.ex.learninghub.modules.admin.dto.response.MentorRequestResponse;
import com.ex.learninghub.modules.admin.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Module", description = "Endpoints for Admin management and Mentor approvals")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/mentor-requests")
    @Operation(summary = "Submit a request to become a mentor (Learner only)")
    public ResponseEntity<ApiResponse<MentorRequestResponse>> submitMentorRequest(
            @RequestParam String bio,
            @RequestParam String experience,
            @RequestParam String skills,
            @AuthenticationPrincipal UserDetails userDetails) {
        MentorRequestResponse response = adminService.submitMentorRequest(
                userDetails.getUsername(), bio, experience, skills
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MentorRequestResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Mentor request submitted successfully")
                        .result(response)
                        .build()
        );
    }

    @GetMapping("/mentor-requests/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get list of all pending mentor requests (Admin only)")
    public ResponseEntity<ApiResponse<List<MentorRequestResponse>>> getPendingRequests() {
        List<MentorRequestResponse> response = adminService.getPendingRequests();
        return ResponseEntity.ok(
                ApiResponse.<List<MentorRequestResponse>>builder()
                        .code(HttpStatus.OK.value())
                        .message("Pending requests retrieved successfully")
                        .result(response)
                        .build()
        );
    }

    @PutMapping("/mentor-requests/{requestId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve or Reject a mentor request (Admin only)")
    public ResponseEntity<ApiResponse<MentorRequestResponse>> processMentorRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody MentorApprovalRequest request) {
        MentorRequestResponse response = adminService.processMentorRequest(requestId, request);
        return ResponseEntity.ok(
                ApiResponse.<MentorRequestResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("Mentor request processed successfully")
                        .result(response)
                        .build()
        );
    }
}
