package com.ex.learninghub.modules.user.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.user.dto.request.AdminClassRequest;
import com.ex.learninghub.modules.user.dto.response.AdminClassResponse;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.entity.AdministrativeClass;
import com.ex.learninghub.modules.user.repository.AdministrativeClassRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.modules.user.service.AdminClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminClassServiceImpl implements AdminClassService {

    private final AdministrativeClassRepository adminClassRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AdminClassResponse createAdminClass(AdminClassRequest request) {
        if (adminClassRepository.existsByClassName(request.getClassName())) {
            throw new AppException(ErrorCode.ADMIN_CLASS_ALREADY_EXISTS);
        }
        AdministrativeClass ac = AdministrativeClass.builder()
                .className(request.getClassName())
                .faculty(request.getFaculty())
                .academicYear(request.getAcademicYear())
                .build();
        return AdminClassResponse.from(adminClassRepository.save(ac));
    }

    @Override
    @Transactional
    public AdminClassResponse updateAdminClass(Long id, AdminClassRequest request) {
        AdministrativeClass ac = adminClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_CLASS_NOT_FOUND));
        ac.setClassName(request.getClassName());
        ac.setFaculty(request.getFaculty());
        ac.setAcademicYear(request.getAcademicYear());
        return AdminClassResponse.from(adminClassRepository.save(ac));
    }

    @Override
    @Transactional
    public void deleteAdminClass(Long id) {
        AdministrativeClass ac = adminClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_CLASS_NOT_FOUND));
        adminClassRepository.delete(ac);
    }

    @Override
    public List<AdminClassResponse> getAllAdminClasses() {
        return adminClassRepository.findAll().stream()
                .map(AdminClassResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public AdminClassResponse getAdminClassById(Long id) {
        return AdminClassResponse.from(adminClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_CLASS_NOT_FOUND)));
    }

    @Override
    public List<UserResponse> getStudentsByAdminClass(Long id) {
        adminClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_CLASS_NOT_FOUND));
        return userRepository.findByAdminClassId(id).stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }
}
