package com.ex.learninghub.modules.enrollment.service.impl;

import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.dto.response.LessonProgressItem;
import com.ex.learninghub.modules.enrollment.dto.response.ProgressResponse;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.entity.LessonProgress;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.enrollment.service.ProgressService;
import com.ex.learninghub.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void markLessonCompleted(Long enrollmentId, Long lessonId, UserPrincipal principal) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // Only the enrolled student can mark their own progress
        if (!enrollment.getStudent().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        Long enrollmentClazzId = enrollment.getClazz().getId();
        boolean lessonInClazz = lessonRepository.findByClazzId(enrollmentClazzId).stream()
                .anyMatch(l -> l.getId().equals(lessonId));
        if (!lessonInClazz) {
            throw new AppException(ErrorCode.LESSON_NOT_IN_ENROLLMENT);
        }

        LessonProgress progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.PROGRESS_NOT_FOUND));

        progress.setIsCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.save(progress);
        
        // Notify student about lesson completion
        notificationService.notifyUser(
            enrollment.getStudent().getId(),
            NotificationType.LESSON_COMPLETED,
            "Bài học đã hoàn thành: " + lesson.getTitle(),
            "Bạn đã hoàn thành bài học " + lesson.getTitle(),
            lesson.getId()
        );
        
        // Notify lecturer about student's progress
        if (enrollment.getClazz().getLecturer() != null) {
            notificationService.notifyUser(
                enrollment.getClazz().getLecturer().getId(),
                NotificationType.LESSON_COMPLETED,
                "Sinh viên hoàn thành bài học",
                enrollment.getStudent().getFullName() + " đã hoàn thành: " + lesson.getTitle(),
                lesson.getId()
            );
        }
    }

    @Override
    @Transactional
    public ProgressResponse getProgressByEnrollment(Long enrollmentId, UserPrincipal principal) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        verifyCanView(enrollment, principal);

        // Fetch all lessons in the class and existing progress records
        List<Lesson> clazzLessons = lessonRepository.findByClazzId(enrollment.getClazz().getId());
        List<LessonProgress> existingProgresses = lessonProgressRepository.findByEnrollmentId(enrollmentId);

        java.util.Set<Long> existingLessonIds = existingProgresses.stream()
                .map(p -> p.getLesson().getId())
                .collect(java.util.stream.Collectors.toSet());

        List<LessonProgress> newProgresses = new java.util.ArrayList<>();
        for (Lesson lesson : clazzLessons) {
            if (!existingLessonIds.contains(lesson.getId())) {
                LessonProgress lp = LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .isCompleted(false)
                        .build();
                newProgresses.add(lp);
            }
        }

        if (!newProgresses.isEmpty()) {
            lessonProgressRepository.saveAll(newProgresses);
            existingProgresses = new java.util.ArrayList<>(existingProgresses);
            existingProgresses.addAll(newProgresses);
        }

        long completed = existingProgresses.stream().filter(p -> Boolean.TRUE.equals(p.getIsCompleted())).count();
        long total = clazzLessons.size();

        List<LessonProgressItem> items = existingProgresses.stream()
                .map(p -> LessonProgressItem.builder()
                        .lessonId(p.getLesson().getId())
                        .lessonTitle(p.getLesson().getTitle())
                        .isCompleted(Boolean.TRUE.equals(p.getIsCompleted()))
                        .completedAt(p.getCompletedAt())
                        .build())
                .toList();

        double percentage = total > 0 ? Math.round((completed * 100.0 / total) * 10.0) / 10.0 : 0.0;

        return ProgressResponse.builder()
                .enrollmentId(enrollmentId)
                .clazzId(enrollment.getClazz().getId())
                .completedCount(completed)
                .totalCount(total)
                .percentage(percentage)
                .lessons(items)
                .build();
    }

    private void verifyCanView(Enrollment enrollment, UserPrincipal principal) {
        Role role = principal.getUser().getRole();
        if (role == Role.ADMIN) {
            return;
        }
        Long userId = principal.getUser().getId();
        // The student themselves
        if (enrollment.getStudent().getId().equals(userId)) {
            return;
        }
        // Lecturer who owns the clazz
        if (role == Role.LECTURER && enrollment.getClazz().getLecturer() != null
                && enrollment.getClazz().getLecturer().getId().equals(userId)) {
            return;
        }
        throw new AppException(ErrorCode.FORBIDDEN);
    }
}
