package com.ex.learninghub.modules.enrollment.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.enrollment.dto.request.EnrollStudentsRequest;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.entity.LessonProgress;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.enrollment.service.ClazzEnrollmentService;
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
public class ClazzEnrollmentServiceImpl implements ClazzEnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ClazzRepository clazzRepository;
    private final UserRepository userRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public void enrollStudents(Long clazzId, EnrollStudentsRequest request) {
        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        // Check max students constraint
        if (clazz.getMaxStudents() != null) {
            long currentCount = enrollmentRepository.countByClazzId(clazzId);
            int newCount = request.getStudentIds() != null ? request.getStudentIds().size() : 0;
            if (currentCount + newCount > clazz.getMaxStudents()) {
                throw new AppException(ErrorCode.CLAZZ_FULL);
            }
        }

        if (request.getStudentIds() != null) {
            List<Lesson> clazzLessons = lessonRepository.findByClazzId(clazzId);
            for (Long studentId : request.getStudentIds()) {
                if (!enrollmentRepository.existsByStudentIdAndClazzId(studentId, clazzId)) {
                    User student = userRepository.findById(studentId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                    Enrollment enrollment = Enrollment.builder()
                            .student(student)
                            .clazz(clazz)
                            .build();
                    Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

                    // Auto-create progress records for every lesson of the clazz
                    if (!clazzLessons.isEmpty()) {
                        List<LessonProgress> progressRecords = clazzLessons.stream()
                                .map(lesson -> LessonProgress.builder()
                                        .enrollment(savedEnrollment)
                                        .lesson(lesson)
                                        .isCompleted(false)
                                        .build())
                                .toList();
                        lessonProgressRepository.saveAll(progressRecords);
                    }
                }
            }
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
