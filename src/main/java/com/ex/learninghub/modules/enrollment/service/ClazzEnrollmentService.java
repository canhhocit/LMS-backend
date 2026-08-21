package com.ex.learninghub.modules.enrollment.service;

import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.enrollment.dto.request.EnrollStudentsRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;

import java.util.List;

/**
 * Service interface for clazz-based enrollment operations.
 */
public interface ClazzEnrollmentService {

    void enrollStudents(Long clazzId, EnrollStudentsRequest request);

    void removeStudent(Long clazzId, Long studentId);

    List<UserResponse> getStudentsInClazz(Long clazzId);

    List<ClazzResponse> getClazzesOfStudent(Long studentId);
}
