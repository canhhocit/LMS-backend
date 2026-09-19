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
    private final com.ex.learninghub.modules.curriculum.repository.CurriculumRepository curriculumRepository;

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
        if (request.getCurriculumId() != null) {
            curriculumRepository.findById(request.getCurriculumId())
                    .ifPresent(ac::setCurriculum);
        }
        if (request.getHomeroomTeacherId() != null) {
            userRepository.findById(request.getHomeroomTeacherId())
                    .ifPresent(ac::setHomeroomTeacher);
        }
        return AdminClassResponse.from(adminClassRepository.save(ac), 0);
    }

    @Override
    @Transactional
    public AdminClassResponse updateAdminClass(Long id, AdminClassRequest request) {
        AdministrativeClass ac = adminClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_CLASS_NOT_FOUND));
        ac.setClassName(request.getClassName());
        ac.setFaculty(request.getFaculty());
        ac.setAcademicYear(request.getAcademicYear());
        if (request.getCurriculumId() != null) {
            ac.setCurriculum(curriculumRepository.findById(request.getCurriculumId()).orElse(null));
        } else {
            ac.setCurriculum(null);
        }
        if (request.getHomeroomTeacherId() != null) {
            ac.setHomeroomTeacher(userRepository.findById(request.getHomeroomTeacherId()).orElse(null));
        } else {
            ac.setHomeroomTeacher(null);
        }
        int count = (int) userRepository.countByAdminClassId(id);
        return AdminClassResponse.from(adminClassRepository.save(ac), count);
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
                .map(ac -> AdminClassResponse.from(ac, (int) userRepository.countByAdminClassId(ac.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public AdminClassResponse getAdminClassById(Long id) {
        AdministrativeClass ac = adminClassRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMIN_CLASS_NOT_FOUND));
        return AdminClassResponse.from(ac, (int) userRepository.countByAdminClassId(id));
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
