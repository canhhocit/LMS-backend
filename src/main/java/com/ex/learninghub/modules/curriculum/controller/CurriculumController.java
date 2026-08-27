package com.ex.learninghub.modules.curriculum.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.curriculum.dto.request.CurriculumCourseRequest;
import com.ex.learninghub.modules.curriculum.dto.request.CurriculumRequest;
import com.ex.learninghub.modules.curriculum.dto.request.PrerequisiteRequest;
import com.ex.learninghub.modules.curriculum.dto.response.CurriculumCourseResponse;
import com.ex.learninghub.modules.curriculum.dto.response.CurriculumResponse;
import com.ex.learninghub.modules.curriculum.dto.response.PrerequisiteResponse;
import com.ex.learninghub.modules.curriculum.service.CurriculumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Chương trình khung", description = "Quản lý chương trình khung, môn học trong CTĐT và môn tiên quyết")
public class CurriculumController {

    private final CurriculumService curriculumService;

    // ========== ADMIN: Curriculum CRUD ==========
    @PostMapping("/admin/curricula")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo chương trình khung mới", description = "Admin tạo mới một chương trình khung (Curriculum) theo từng ngành/khoa.")
    public ResponseEntity<ApiResponse<CurriculumResponse>> create(@Valid @RequestBody CurriculumRequest req) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.createCurriculum(req)));
    }

    @PutMapping("/admin/curricula/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật chương trình khung", description = "Admin cập nhật thông tin (tên, khoa, năm học, ...) của một chương trình khung.")
    public ResponseEntity<ApiResponse<CurriculumResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody CurriculumRequest req) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.updateCurriculum(id, req)));
    }

    @DeleteMapping("/admin/curricula/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa chương trình khung", description = "Admin xóa một chương trình khung.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/admin/curricula")
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
    @Operation(summary = "Lấy danh sách chương trình khung", description = "Trả về danh sách tất cả chương trình khung trong hệ thống.")
    public ResponseEntity<ApiResponse<List<CurriculumResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.listCurricula()));
    }

    @GetMapping("/curricula/{id}")
    @Operation(summary = "Lấy chi tiết chương trình khung", description = "Trả về thông tin chi tiết của một chương trình khung.")
    public ResponseEntity<ApiResponse<CurriculumResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.getCurriculum(id)));
    }

    // ========== ADMIN: CurriculumCourse ==========
    @PostMapping("/admin/curricula/{curriculumId}/courses")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Thêm môn học vào chương trình khung", description = "Admin thêm một môn học (kèm thông tin: tín chỉ, học kỳ gợi ý, ...) vào chương trình khung.")
    public ResponseEntity<ApiResponse<CurriculumCourseResponse>> addCourse(@PathVariable Long curriculumId,
                                                                          @Valid @RequestBody CurriculumCourseRequest req) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.addCourseToCurriculum(curriculumId, req)));
    }

    @DeleteMapping("/admin/curricula/{curriculumId}/courses/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa môn học khỏi chương trình khung", description = "Admin gỡ một môn học ra khỏi chương trình khung.")
    public ResponseEntity<ApiResponse<Void>> removeCourse(@PathVariable Long curriculumId,
                                                          @PathVariable Long courseId) {
        curriculumService.removeCourseFromCurriculum(curriculumId, courseId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/curricula/{curriculumId}/courses")
    @Operation(summary = "Lấy danh sách môn học của chương trình khung", description = "Trả về danh sách các môn học trong một chương trình khung.")
    public ResponseEntity<ApiResponse<List<CurriculumCourseResponse>>> listCourses(@PathVariable Long curriculumId) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.listCoursesByCurriculum(curriculumId)));
    }

    // ========== ADMIN: Prerequisite ==========
    @PostMapping("/admin/courses/{courseId}/prerequisites")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Thêm môn tiên quyết cho môn học", description = "Admin thêm một môn tiên quyết cho một môn học cụ thể.")
    public ResponseEntity<ApiResponse<PrerequisiteResponse>> addPrereq(@PathVariable Long courseId,
                                                                      @Valid @RequestBody PrerequisiteRequest req) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.addPrerequisite(courseId, req)));
    }

    @DeleteMapping("/admin/courses/{courseId}/prerequisites/{prereqId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa môn tiên quyết", description = "Admin gỡ quan hệ tiên quyết giữa hai môn học.")
    public ResponseEntity<ApiResponse<Void>> removePrereq(@PathVariable Long courseId,
                                                          @PathVariable Long prereqId) {
        curriculumService.removePrerequisite(courseId, prereqId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/courses/{courseId}/prerequisites")
    @Operation(summary = "Lấy danh sách môn tiên quyết của một môn học", description = "Trả về danh sách các môn tiên quyết của một môn học cụ thể.")
    public ResponseEntity<ApiResponse<List<PrerequisiteResponse>>> listPrereqs(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.listPrerequisites(courseId)));
    }

    // ========== STUDENT: check trước khi đăng ký ==========
    @GetMapping("/courses/{courseId}/prerequisites/check")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Kiểm tra môn tiên quyết còn thiếu", description = "Trả về danh sách ID các môn tiên quyết mà sinh viên hiện tại chưa hoàn thành để đăng ký môn học này.")
    public ResponseEntity<ApiResponse<List<Long>>> checkPrereqs(@PathVariable Long courseId,
                                                                @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                curriculumService.checkMissingPrerequisites(courseId, principal)));
    }
}
