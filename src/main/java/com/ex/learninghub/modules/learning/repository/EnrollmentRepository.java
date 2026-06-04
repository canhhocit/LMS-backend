package com.ex.learninghub.modules.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ex.learninghub.modules.learning.entity.Enrollment;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByLearnerId(Long learnerId);
    Optional<Enrollment> findByLearnerIdAndCourseId(Long learnerId, Long courseId);
    boolean existsByLearnerIdAndCourseId(Long learnerId, Long courseId);
}
