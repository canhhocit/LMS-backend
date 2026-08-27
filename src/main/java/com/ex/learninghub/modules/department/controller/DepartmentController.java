package com.ex.learninghub.modules.department.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.department.dto.request.DepartmentRequest;
import com.ex.learninghub.modules.department.dto.response.DepartmentResponse;
import com.ex.learninghub.modules.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/departments")
@RequiredArgsConstructor
@Tag(name = "Quản trị - Khoa/Bộ môn", description = "Các API CRUD quản lý khoa/bộ môn trong hệ thống")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Tạo khoa/bộ môn mới",
            description = "Tạo mới một khoa/bộ môn trong hệ thống."
    )
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(@Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.create(req)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cập nhật khoa/bộ môn",
            description = "Cập nhật thông tin (tên, mã, mô tả, trưởng khoa) của một khoa/bộ môn."
    )
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @Parameter(description = "ID của khoa/bộ môn", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest req) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Xóa khoa/bộ môn",
            description = "Xóa một khoa/bộ môn khỏi hệ thống."
    )
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID của khoa/bộ môn cần xóa", example = "1")
            @PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
    @Operation(
            summary = "Lấy danh sách khoa/bộ môn",
            description = "Trả về danh sách tất cả khoa/bộ môn đang hoạt động trong hệ thống."
    )
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(departmentService.list()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
    @Operation(
            summary = "Lấy chi tiết khoa/bộ môn",
            description = "Trả về thông tin chi tiết của một khoa/bộ môn theo ID."
    )
    public ResponseEntity<ApiResponse<DepartmentResponse>> get(
            @Parameter(description = "ID của khoa/bộ môn", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.get(id)));
    }
}
