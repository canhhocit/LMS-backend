package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.user.dto.request.AdminClassRequest;
import com.ex.learninghub.modules.user.dto.response.AdminClassResponse;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.service.AdminClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/administrative-classes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Quản trị - Lớp hành chính", description = "Các API CRUD cho lớp hành chính, quản lý danh sách sinh viên của lớp")
public class AdministrativeClassController {

    private final AdminClassService adminClassService;

    @PostMapping
    @Operation(
            summary = "Tạo lớp hành chính",
            description = "Tạo mới một lớp hành chính trong hệ thống."
    )
    public ApiResponse<AdminClassResponse> create(@Valid @RequestBody AdminClassRequest request) {
        return ApiResponse.success(adminClassService.createAdminClass(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật lớp hành chính",
            description = "Cập nhật thông tin (tên, khoa, cố vấn học tập, ...) của một lớp hành chính."
    )
    public ApiResponse<AdminClassResponse> update(
            @Parameter(description = "ID của lớp hành chính", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AdminClassRequest request) {
        return ApiResponse.success(adminClassService.updateAdminClass(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Xóa lớp hành chính",
            description = "Xóa một lớp hành chính khỏi hệ thống."
    )
    public ApiResponse<Void> delete(
            @Parameter(description = "ID của lớp hành chính cần xóa", example = "1")
            @PathVariable Long id) {
        adminClassService.deleteAdminClass(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả lớp hành chính",
            description = "Trả về danh sách tất cả lớp hành chính trong hệ thống."
    )
    public ApiResponse<List<AdminClassResponse>> getAll() {
        return ApiResponse.success(adminClassService.getAllAdminClasses());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy chi tiết lớp hành chính",
            description = "Trả về thông tin chi tiết của một lớp hành chính theo ID."
    )
    public ApiResponse<AdminClassResponse> getById(
            @Parameter(description = "ID của lớp hành chính", example = "1")
            @PathVariable Long id) {
        return ApiResponse.success(adminClassService.getAdminClassById(id));
    }

    @GetMapping("/{id}/students")
    @Operation(
            summary = "Lấy danh sách sinh viên của lớp hành chính",
            description = "Trả về danh sách UserResponse của các sinh viên thuộc lớp hành chính."
    )
    public ApiResponse<List<UserResponse>> getStudents(
            @Parameter(description = "ID của lớp hành chính", example = "1")
            @PathVariable Long id) {
        return ApiResponse.success(adminClassService.getStudentsByAdminClass(id));
    }
}
