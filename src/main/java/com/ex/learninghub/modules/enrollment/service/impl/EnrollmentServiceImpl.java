package com.ex.learninghub.modules.enrollment.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.enrollment.dto.request.EnrollStudentsRequest;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.service.EnrollmentService;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ClazzRepository clazzRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void enrollStudents(Long clazzId, EnrollStudentsRequest request) {
        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        for (Long studentId : request.getStudentIds()) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            if (enrollmentRepository.existsByStudentIdAndClazzId(studentId, clazzId)) {
                continue;
            }
            Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .clazz(clazz)
                    .build();
            enrollmentRepository.save(enrollment);
        }
    }

    @Override
    @Transactional
    public void removeStudent(Long clazzId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndClazzId(studentId, clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public List<UserResponse> getStudentsInClazz(Long clazzId) {
        return enrollmentRepository.findByClazzId(clazzId).stream()
                .map(e -> UserResponse.from(e.getStudent()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClazzResponse> getClazzesOfStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(e -> ClazzResponse.from(e.getClazz()))
                .collect(Collectors.toList());
    }
}
