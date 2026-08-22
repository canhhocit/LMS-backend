package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.user.dto.request.AdminClassRequest;
import com.ex.learninghub.modules.user.dto.response.AdminClassResponse;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.service.AdminClassService;
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
public class AdministrativeClassController {

    private final AdminClassService adminClassService;

    @PostMapping
    public ApiResponse<AdminClassResponse> create(@Valid @RequestBody AdminClassRequest request) {
        return ApiResponse.success(adminClassService.createAdminClass(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminClassResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody AdminClassRequest request) {
        return ApiResponse.success(adminClassService.updateAdminClass(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminClassService.deleteAdminClass(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<List<AdminClassResponse>> getAll() {
        return ApiResponse.success(adminClassService.getAllAdminClasses());
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminClassResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(adminClassService.getAdminClassById(id));
    }

    @GetMapping("/{id}/students")
    public ApiResponse<List<UserResponse>> getStudents(@PathVariable Long id) {
        return ApiResponse.success(adminClassService.getStudentsByAdminClass(id));
    }
}
