package com.ex.learninghub.modules.course.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.course.dto.request.CourseRequest;
import com.ex.learninghub.modules.course.dto.response.CourseResponse;
import com.ex.learninghub.modules.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Quản trị - Khóa học", description = "Các API CRUD khóa học (Course) dành cho Admin")
public class AdminCourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(
            summary = "Tạo khóa học mới",
            description = "Tạo một khóa học mới trong hệ thống (thông tin chung: mã, tên, tín chỉ, mô tả, ...)."
    )
    public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ApiResponse.success(courseService.createCourse(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin khóa học",
            description = "Cập nhật thông tin chung của một khóa học đã tồn tại."
    )
    public ApiResponse<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        return ApiResponse.success(courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Xóa khóa học",
            description = "Xóa một khóa học khỏi hệ thống."
    )
    public ApiResponse<Void> deleteCourse(
            @PathVariable Long id) {
        courseService.deleteCourse(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả khóa học",
            description = "Trả về toàn bộ danh sách khóa học trong hệ thống (không phân trang)."
    )
    public ApiResponse<List<CourseResponse>> getAllCourses() {
        return ApiResponse.success(courseService.getAllCourses());
    }

    @GetMapping("/paged")
    @Operation(
            summary = "Lấy danh sách khóa học (phân trang)",
            description = "Trả về danh sách khóa học theo trang, hỗ trợ sắp xếp theo các trường."
    )
    public ApiResponse<Page<CourseResponse>> getAllCoursesPaged(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.success(courseService.getAllCourses(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy chi tiết một khóa học",
            description = "Trả về thông tin chi tiết của khóa học theo ID."
    )
    public ApiResponse<CourseResponse> getCourseById(
            @PathVariable Long id) {
        return ApiResponse.success(courseService.getCourseById(id));
    }
}
