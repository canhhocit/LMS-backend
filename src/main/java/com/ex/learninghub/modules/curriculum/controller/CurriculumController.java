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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    // ========== ADMIN: Curriculum CRUD ==========
    @PostMapping("/admin/curricula")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CurriculumResponse>> create(@Valid @RequestBody CurriculumRequest req) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.createCurriculum(req)));
    }

    @PutMapping("/admin/curricula/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CurriculumResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody CurriculumRequest req) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.updateCurriculum(id, req)));
    }

    @DeleteMapping("/admin/curricula/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/admin/curricula")
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
    public ResponseEntity<ApiResponse<List<CurriculumResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.listCurricula()));
    }

    @GetMapping("/curricula/{id}")
    public ResponseEntity<ApiResponse<CurriculumResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.getCurriculum(id)));
    }

    // ========== ADMIN: CurriculumCourse ==========
    @PostMapping("/admin/curricula/{curriculumId}/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CurriculumCourseResponse>> addCourse(@PathVariable Long curriculumId,
                                                                          @Valid @RequestBody CurriculumCourseRequest req) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.addCourseToCurriculum(curriculumId, req)));
    }

    @DeleteMapping("/admin/curricula/{curriculumId}/courses/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeCourse(@PathVariable Long curriculumId,
                                                          @PathVariable Long courseId) {
        curriculumService.removeCourseFromCurriculum(curriculumId, courseId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/curricula/{curriculumId}/courses")
    public ResponseEntity<ApiResponse<List<CurriculumCourseResponse>>> listCourses(@PathVariable Long curriculumId) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.listCoursesByCurriculum(curriculumId)));
    }

    // ========== ADMIN: Prerequisite ==========
    @PostMapping("/admin/courses/{courseId}/prerequisites")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PrerequisiteResponse>> addPrereq(@PathVariable Long courseId,
                                                                      @Valid @RequestBody PrerequisiteRequest req) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.addPrerequisite(courseId, req)));
    }

    @DeleteMapping("/admin/courses/{courseId}/prerequisites/{prereqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removePrereq(@PathVariable Long courseId,
                                                          @PathVariable Long prereqId) {
        curriculumService.removePrerequisite(courseId, prereqId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/courses/{courseId}/prerequisites")
    public ResponseEntity<ApiResponse<List<PrerequisiteResponse>>> listPrereqs(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success(curriculumService.listPrerequisites(courseId)));
    }

    // ========== STUDENT: check trước khi đăng ký ==========
    @GetMapping("/courses/{courseId}/prerequisites/check")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<Long>>> checkPrereqs(@PathVariable Long courseId,
                                                                @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                curriculumService.checkMissingPrerequisites(courseId, principal)));
    }
}
