package com.ex.learninghub.modules.enrollment.repository;

import com.ex.learninghub.modules.enrollment.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.enrollmentId = :enrollmentId AND lp.isCompleted = true")
    long countCompletedByEnrollmentId(Long enrollmentId);

    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.enrollmentId = :enrollmentId")
    long countTotalByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentIdAndLessonIdAndIsCompletedTrue(Long enrollmentId, Long lessonId);
}