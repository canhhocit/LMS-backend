package com.ex.learninghub.modules.admin.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        return ApiResponse.success(adminService.getDashboardStats());
    }
}
