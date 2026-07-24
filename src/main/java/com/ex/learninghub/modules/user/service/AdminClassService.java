package com.ex.learninghub.modules.user.service;

import com.ex.learninghub.modules.user.dto.request.AdminClassRequest;
import com.ex.learninghub.modules.user.dto.response.AdminClassResponse;
import com.ex.learninghub.modules.user.dto.response.UserResponse;

import java.util.List;

public interface AdminClassService {
    AdminClassResponse createAdminClass(AdminClassRequest request);
    AdminClassResponse updateAdminClass(Long id, AdminClassRequest request);
    void deleteAdminClass(Long id);
    List<AdminClassResponse> getAllAdminClasses();
    AdminClassResponse getAdminClassById(Long id);
    List<UserResponse> getStudentsByAdminClass(Long id);
}
