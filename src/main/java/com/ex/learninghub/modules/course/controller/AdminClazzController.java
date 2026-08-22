package com.ex.learninghub.modules.course.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.course.dto.request.ClazzRequest;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.service.ClazzService;
import com.ex.learninghub.modules.enrollment.dto.request.EnrollStudentsRequest;
import com.ex.learninghub.modules.enrollment.service.ClazzEnrollmentService;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/classes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminClazzController {

    private final ClazzService clazzService;
    private final ClazzEnrollmentService enrollmentService;

    @PostMapping
    public ApiResponse<ClazzResponse> createClazz(@Valid @RequestBody ClazzRequest request) {
        return ApiResponse.success(clazzService.createClazz(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ClazzResponse> updateClazz(@PathVariable Long id,
                                                    @Valid @RequestBody ClazzRequest request) {
        return ApiResponse.success(clazzService.updateClazz(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteClazz(@PathVariable Long id) {
        clazzService.deleteClazz(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<List<ClazzResponse>> getAllClazzes() {
        return ApiResponse.success(clazzService.getAllClazzes());
    }

    @GetMapping("/paged")
    public ApiResponse<Page<ClazzResponse>> getAllClazzesPaged(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.success(clazzService.getAllClazzes(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ClazzResponse> getClazzById(@PathVariable Long id) {
        return ApiResponse.success(clazzService.getClazzById(id));
    }

    @PostMapping("/{id}/enroll")
    public ApiResponse<Void> enrollStudents(@PathVariable Long id,
                                             @Valid @RequestBody EnrollStudentsRequest request) {
        enrollmentService.enrollStudents(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    public ApiResponse<Void> removeStudent(@PathVariable Long classId, @PathVariable Long studentId) {
        enrollmentService.removeStudent(classId, studentId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/students")
    public ApiResponse<List<UserResponse>> getStudentsInClazz(@PathVariable Long id) {
        return ApiResponse.success(enrollmentService.getStudentsInClazz(id));
    }

    @GetMapping("/lecturer/{lecturerId}")
    public ApiResponse<List<ClazzResponse>> getClazzesByLecturer(@PathVariable Long lecturerId) {
        return ApiResponse.success(clazzService.getClazzesByLecturer(lecturerId));
    }
}
