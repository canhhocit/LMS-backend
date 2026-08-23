package com.ex.learninghub.modules.department.service;

import com.ex.learninghub.modules.department.dto.request.DepartmentRequest;
import com.ex.learninghub.modules.department.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse update(Long id, DepartmentRequest request);
    void delete(Long id);
    List<DepartmentResponse> list();
    DepartmentResponse get(Long id);
}
