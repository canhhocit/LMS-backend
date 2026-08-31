package com.ex.learninghub.modules.assessment.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.dto.request.AssignmentRequest;
import com.ex.learninghub.modules.assessment.dto.request.GradeSubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.request.SubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.response.AssignmentResponse;
import com.ex.learninghub.modules.assessment.dto.response.SubmissionResponse;
import com.ex.learninghub.modules.assessment.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Bài tập & Nộp bài", description = "Các API quản lý bài tập (Assignment) và bài nộp (Submission) của sinh viên")
public class AssessmentController {

    private final AssessmentService assessmentService;

    // ─── Assignments ─────────────────────────────────────────────────────────────

    @PostMapping("/classes/{classId}/assignments")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Tạo bài tập mới",
            description = "Giảng viên tạo một bài tập mới cho lớp học phần của mình."
    )
    public ApiResponse<AssignmentResponse> createAssignment(
            @PathVariable Long classId,
            @Valid @RequestBody AssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.createAssignment(classId, request, userPrincipal));
    }

    @PutMapping("/assignments/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Cập nhật bài tập",
            description = "Giảng viên cập nhật nội dung, hạn nộp hoặc thông tin khác của một bài tập."
    )
    public ApiResponse<AssignmentResponse> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.updateAssignment(id, request, userPrincipal));
    }

    @DeleteMapping("/assignments/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Xóa bài tập",
            description = "Giảng viên xóa một bài tập. Lưu ý: các bài nộp liên quan cũng sẽ bị xóa theo."
    )
    public ApiResponse<Void> deleteAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        assessmentService.deleteAssignment(id, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/classes/{classId}/assignments")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Lấy danh sách bài tập của lớp học phần",
            description = "Trả về danh sách tất cả bài tập thuộc về một lớp học phần."
    )
    public ApiResponse<List<AssignmentResponse>> getAssignmentsByClass(
            @PathVariable Long classId) {
        return ApiResponse.success(assessmentService.getAssignmentsByClass(classId));
    }

    // ─── Submissions ─────────────────────────────────────────────────────────────

    @PostMapping("/assignments/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Sinh viên nộp bài tập",
            description = "Sinh viên nộp bài làm cho một bài tập. Nộp lại sẽ cập nhật bài nộp gần nhất (tùy chính sách)."
    )
    public ApiResponse<SubmissionResponse> submitAssignment(
            @PathVariable Long id,
            @Valid @RequestBody SubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.submitAssignment(id, request, userPrincipal));
    }

    @PostMapping("/assignments/{id}/submit-file")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Upload file bài nộp lên Cloudinary",
            description = "Sinh viên upload file bài nộp cho một bài tập và nhận về URL đã lưu trên Cloudinary."
    )
    public ApiResponse<String> uploadSubmissionFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.uploadSubmissionFile(id, file, userPrincipal));
    }

    @PostMapping("/assignments/{id}/submit-files")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Upload nhiều file bài nộp lên Cloudinary",
            description = "Sinh viên upload nhiều file/folder content cho cùng một bài tập và nhận về danh sách URL đã lưu trên Cloudinary."
    )
    public ApiResponse<List<String>> uploadSubmissionFiles(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.uploadSubmissionFiles(id, files, userPrincipal));
    }

    @PutMapping("/submissions/{id}/grade")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Chấm điểm bài nộp",
            description = "Giảng viên chấm điểm và nhận xét cho một bài nộp của sinh viên."
    )
    public ApiResponse<SubmissionResponse> gradeSubmission(
            @PathVariable Long id,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.gradeSubmission(id, request, userPrincipal));
    }

    @GetMapping("/assignments/{id}/submissions")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Lấy danh sách bài nộp theo bài tập",
            description = "Giảng viên xem tất cả bài nộp của sinh viên cho một bài tập cụ thể."
    )
    public ApiResponse<List<SubmissionResponse>> getSubmissionsByAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.getSubmissionsByAssignment(id, userPrincipal));
    }

    @GetMapping("/me/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Lấy danh sách bài nộp của tôi",
            description = "Sinh viên xem lại tất cả bài nộp của mình (kèm điểm và nhận xét nếu có)."
    )
    public ApiResponse<List<SubmissionResponse>> getMySubmissions(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(assessmentService.getMySubmissions(userPrincipal));
    }
}
