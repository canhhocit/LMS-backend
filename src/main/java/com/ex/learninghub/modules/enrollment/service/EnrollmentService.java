package com.ex.learninghub.modules.enrollment.service;

import com.ex.learninghub.modules.enrollment.dto.CreateEnrollmentDTO;
import com.ex.learninghub.modules.enrollment.dto.EnrollmentDTO;
import com.ex.learninghub.modules.enrollment.dto.ProgressDTO;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.entity.LessonProgress;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.enums.CourseStatus;
import com.ex.learninghub.common.enums.EnrollmentStatus;
import com.ex.learninghub.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    /**
     * Enroll learner in a course
     * FR-ENROLL-01: Create enrollment and initialize progress for all lessons
     */
    @Transactional
    public EnrollmentDTO enroll(Long learnerId, Long courseId) {
        // Check if course exists and is published
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new AppException(ErrorCode.COURSE_NOT_PUBLISHED);
        }

        // Check if already enrolled
        if (enrollmentRepository.existsByLearnerIdAndCourseId(learnerId, courseId)) {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTS);
        }

        // Get learner
        User learner = userRepository.findById(learnerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Create enrollment
        Enrollment enrollment = Enrollment.builder()
                .learner(learner)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(LocalDateTime.now())
                .build();
        enrollment = enrollmentRepository.save(enrollment);

        // Initialize progress for all lessons in the course
        List<Lesson> lessons = lessonRepository.findByChapterCourseId(courseId);
        List<LessonProgress> progressList = new ArrayList<>();
        
        for (Lesson lesson : lessons) {
            LessonProgress progress = LessonProgress.builder()
                    .enrollmentId(enrollment.getId())
                    .lessonId(lesson.getId())
                    .isCompleted(false)
                    .build();
            progressList.add(progress);
        }
        
        if (!progressList.isEmpty()) {
            lessonProgressRepository.saveAll(progressList);
        }

        return mapToDTO(enrollment);
    }

    /**
     * Get enrollments for a learner
     */
    public List<EnrollmentDTO> getEnrollmentsByLearner(Long learnerId) {
        return enrollmentRepository.findByLearnerId(learnerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get enrollment by ID
     */
    public EnrollmentDTO getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        return mapToDTO(enrollment);
    }

    /**
     * Get enrollment by learner and course
     */
    public EnrollmentDTO getEnrollmentByLearnerAndCourse(Long learnerId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByLearnerIdAndCourseId(learnerId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        return mapToDTO(enrollment);
    }

    /**
     * Get progress for an enrollment
     * FR-ENROLL-02: Calculate and return progress percentage
     */
    public ProgressDTO getProgress(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        long completed = lessonProgressRepository.countCompletedByEnrollmentId(enrollmentId);
        long total = lessonProgressRepository.countTotalByEnrollmentId(enrollmentId);
        
        double percentage = total > 0 ? (completed * 100.0 / total) : 0;

        return ProgressDTO.builder()
                .enrollmentId(enrollmentId)
                .totalLessons(total)
                .completedLessons(completed)
                .percentage(percentage)
                .build();
    }

    /**
     * Mark a lesson as completed
     * FR-ENROLL-02: Update progress and check for completion
     */
    @Transactional
    public ProgressDTO markLessonCompleted(Long learnerId, Long lessonId) {
        // Find the lesson
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // Find the enrollment
        Enrollment enrollment = enrollmentRepository.findByLearnerIdAndCourseId(learnerId, lesson.getChapter().getCourse().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // Check if progress record exists
        LessonProgress progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                .orElse(null);

        if (progress == null) {
            // Create new progress record
            progress = LessonProgress.builder()
                    .enrollmentId(enrollment.getId())
                    .lessonId(lessonId)
                    .isCompleted(true)
                    .completedAt(LocalDateTime.now())
                    .build();
        } else {
            // Update existing progress
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }
        
        lessonProgressRepository.save(progress);

        // Check if enrollment is completed (100%)
        return checkAndUpdateEnrollmentCompletion(enrollment.getId());
    }

    /**
     * Check if enrollment is 100% complete and update status
     */
    @Transactional
    public ProgressDTO checkAndUpdateEnrollmentCompletion(Long enrollmentId) {
        long completed = lessonProgressRepository.countCompletedByEnrollmentId(enrollmentId);
        long total = lessonProgressRepository.countTotalByEnrollmentId(enrollmentId);
        
        double percentage = total > 0 ? (completed * 100.0 / total) : 0;

        // If 100% complete, update enrollment status
        if (completed == total && total > 0) {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
        }

        return ProgressDTO.builder()
                .enrollmentId(enrollmentId)
                .totalLessons(total)
                .completedLessons(completed)
                .percentage(percentage)
                .build();
    }

    /**
     * Get students enrolled in a course (for mentor)
     */
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map Enrollment to DTO
     */
    private EnrollmentDTO mapToDTO(Enrollment enrollment) {
        long completed = lessonProgressRepository.countCompletedByEnrollmentId(enrollment.getId());
        long total = lessonProgressRepository.countTotalByEnrollmentId(enrollment.getId());
        double percentage = total > 0 ? (completed * 100.0 / total) : 0;

        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .learnerId(enrollment.getLearner() != null ? enrollment.getLearner().getId() : null)
                .learnerName(enrollment.getLearner() != null ? enrollment.getLearner().getFullName() : null)
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getCourseName() : null)
                .status(enrollment.getStatus())
                .enrolledAt(enrollment.getEnrolledAt() != null ? enrollment.getEnrolledAt().toString() : null)
                .completedAt(enrollment.getCompletedAt() != null ? enrollment.getCompletedAt().toString() : null)
                .progressPercentage(percentage)
                .build();
    }
}