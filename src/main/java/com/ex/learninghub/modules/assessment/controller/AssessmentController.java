package com.ex.learninghub.modules.assessment.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.dto.request.AssignmentRequest;
import com.ex.learninghub.modules.assessment.dto.request.GradeSubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.request.SubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.response.AssignmentResponse;
import com.ex.learninghub.modules.assessment.dto.response.SubmissionResponse;
import com.ex.learninghub.modules.assessment.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    // ─── Assignments ─────────────────────────────────────────────────────────────

    @PostMapping("/classes/{classId}/assignments")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<AssignmentResponse> createAssignment(@PathVariable Long classId,
                                                              @Valid @RequestBody AssignmentRequest request,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.createAssignment(classId, request, userPrincipal));
    }

    @PutMapping("/assignments/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<AssignmentResponse> updateAssignment(@PathVariable Long id,
                                                             @Valid @RequestBody AssignmentRequest request,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.updateAssignment(id, request, userPrincipal));
    }

    @DeleteMapping("/assignments/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<Void> deleteAssignment(@PathVariable Long id,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        assessmentService.deleteAssignment(id, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/classes/{classId}/assignments")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<AssignmentResponse>> getAssignmentsByClass(@PathVariable Long classId) {
        return ApiResponse.success(assessmentService.getAssignmentsByClass(classId));
    }

    // ─── Submissions ─────────────────────────────────────────────────────────────

    @PostMapping("/assignments/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<SubmissionResponse> submitAssignment(@PathVariable Long id,
                                                              @Valid @RequestBody SubmissionRequest request,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.submitAssignment(id, request, userPrincipal));
    }

    @PutMapping("/submissions/{id}/grade")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<SubmissionResponse> gradeSubmission(@PathVariable Long id,
                                                            @Valid @RequestBody GradeSubmissionRequest request,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.gradeSubmission(id, request, userPrincipal));
    }

    @GetMapping("/assignments/{id}/submissions")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<List<SubmissionResponse>> getSubmissionsByAssignment(@PathVariable Long id,
                                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.getSubmissionsByAssignment(id, userPrincipal));
    }

    @GetMapping("/me/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<SubmissionResponse>> getMySubmissions(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.getMySubmissions(userPrincipal));
    }
}
