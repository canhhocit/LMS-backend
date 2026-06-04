package com.ex.learninghub.modules.learning.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.learning.dto.response.EnrollmentResponse;
import com.ex.learninghub.modules.learning.dto.response.ProgressResponse;
import com.ex.learninghub.modules.learning.entity.Enrollment;
import com.ex.learninghub.modules.learning.entity.Progress;
import com.ex.learninghub.modules.learning.repository.EnrollmentRepository;
import com.ex.learninghub.modules.learning.repository.ProgressRepository;
import com.ex.learninghub.modules.learning.service.LearningService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {

    private final EnrollmentRepository enrollmentRepository;
    private final ProgressRepository progressRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EnrollmentResponse enrollCourse(Long courseId, String email) {
        User learner = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (enrollmentRepository.existsByLearnerIdAndCourseId(learner.getId(), courseId)) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTS);
        }

        Enrollment enrollment = Enrollment.builder()
                .learner(learner)
                .course(course)
                .status("ACTIVE")
                .enrolledAt(LocalDateTime.now())
                .build();

        enrollment = enrollmentRepository.save(enrollment);
        return mapToEnrollmentResponse(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String email) {
        User learner = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return enrollmentRepository.findByLearnerId(learner.getId()).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProgressResponse updateProgress(Long enrollmentId, Long lessonId, boolean isCompleted) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        Progress progress = progressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElse(null);

        if (progress == null) {
            progress = Progress.builder()
                    .enrollment(enrollment)
                    .lesson(lesson)
                    .isCompleted(isCompleted)
                    .completedAt(isCompleted ? LocalDateTime.now() : null)
                    .build();
        } else {
            progress.setIsCompleted(isCompleted);
            progress.setCompletedAt(isCompleted ? LocalDateTime.now() : null);
        }

        progress = progressRepository.save(progress);
        return mapToProgressResponse(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressResponse> getProgress(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }
        return progressRepository.findByEnrollmentId(enrollmentId).stream()
                .map(this::mapToProgressResponse)
                .collect(Collectors.toList());
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .learnerId(enrollment.getLearner().getId())
                .courseId(enrollment.getCourse().getId())
                .status(enrollment.getStatus())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .build();
    }

    private ProgressResponse mapToProgressResponse(Progress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(progress.getEnrollment().getId())
                .lessonId(progress.getLesson().getId())
                .isCompleted(progress.getIsCompleted())
                .completedAt(progress.getCompletedAt())
                .build();
    }
}
