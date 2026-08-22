package com.ex.learninghub.modules.enrollment.repository;

import com.ex.learninghub.modules.enrollment.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByEnrollmentId(Long enrollmentId);
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
    long countByEnrollmentIdAndIsCompletedTrue(Long enrollmentId);
    long countByEnrollmentId(Long enrollmentId);
}
