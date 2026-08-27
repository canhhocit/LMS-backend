package com.ex.learninghub.modules.admin.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Quản trị - Tổng quan", description = "Các API dashboard tổng quan dành cho Admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Thống kê tổng quan cho dashboard Admin",
            description = "Trả về các chỉ số tổng quan phục vụ trang dashboard của Admin: tổng số sinh viên, giảng viên, lớp học phần, khóa học, v.v."
    )
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        return ApiResponse.success(adminService.getDashboardStats());
    }
}
