package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.user.service.AdminPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Quản trị phân quyền Admin", description = "API quản lý phân quyền chi tiết cho các tài khoản Admin (RBAC)")
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN') and @adminPermissionService.hasPermission(authentication, 'SYSTEM_CONFIG')")
    @Operation(summary = "Lấy danh sách tất cả các quyền Admin", description = "Trả về mã quyền và mô tả của tất cả AdminPermission trong hệ thống.")
    public ApiResponse<List<Map<String, String>>> getAllPermissions() {
        return ApiResponse.success(adminPermissionService.getAllPermissions());
    }

    @GetMapping("/users/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN') and @adminPermissionService.hasPermission(authentication, 'SYSTEM_CONFIG')")
    @Operation(summary = "Lấy danh sách quyền của một Admin", description = "Trả về danh sách các permission code mà Admin được cấp.")
    public ApiResponse<List<String>> getUserPermissions(@PathVariable Long id) {
        return ApiResponse.success(adminPermissionService.getUserPermissions(id));
    }

    @PutMapping("/users/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN') and @adminPermissionService.hasPermission(authentication, 'SYSTEM_CONFIG')")
    @Operation(summary = "Cập nhật danh sách quyền cho một Admin", description = "Gán lại toàn bộ danh sách permission code cho tài khoản Admin.")
    public ApiResponse<Void> updateUserPermissions(@PathVariable Long id, @RequestBody List<String> permissions) {
        adminPermissionService.updateUserPermissions(id, permissions);
        return ApiResponse.success(null);
    }
}
