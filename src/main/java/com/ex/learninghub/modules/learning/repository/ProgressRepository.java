package com.ex.learninghub.modules.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ex.learninghub.modules.learning.entity.Progress;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByEnrollmentId(Long enrollmentId);
    Optional<Progress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
    long countByEnrollmentIdAndIsCompletedTrue(Long enrollmentId);
}
