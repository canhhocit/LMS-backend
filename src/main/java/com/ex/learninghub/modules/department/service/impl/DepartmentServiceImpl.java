package com.ex.learninghub.modules.department.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.department.dto.request.DepartmentRequest;
import com.ex.learninghub.modules.department.dto.response.DepartmentResponse;
import com.ex.learninghub.modules.department.entity.Department;
import com.ex.learninghub.modules.department.repository.DepartmentRepository;
import com.ex.learninghub.modules.department.service.DepartmentService;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.COURSE_ALREADY_EXISTS); // generic
        }
        if (request.getHeadUserId() != null) {
            userRepository.findById(request.getHeadUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }
        Department d = Department.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .headUserId(request.getHeadUserId())
                .isActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive())
                .build();
        return DepartmentResponse.from(departmentRepository.save(d));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department d = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        d.setName(request.getName());
        d.setDescription(request.getDescription());
        if (request.getIsActive() != null) d.setIsActive(request.getIsActive());
        if (request.getHeadUserId() != null) {
            userRepository.findById(request.getHeadUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            d.setHeadUserId(request.getHeadUserId());
        }
        return DepartmentResponse.from(departmentRepository.save(d));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        departmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> list() {
        return departmentRepository.findAll().stream()
                .map(DepartmentResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse get(Long id) {
        return departmentRepository.findById(id)
                .map(DepartmentResponse::from)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
    }
}
