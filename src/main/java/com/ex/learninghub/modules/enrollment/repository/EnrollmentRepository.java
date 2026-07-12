package com.ex.learninghub.modules.enrollment.repository;

import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByClazzId(Long clazzId);
    Optional<Enrollment> findByStudentIdAndClazzId(Long studentId, Long clazzId);
    boolean existsByStudentIdAndClazzId(Long studentId, Long clazzId);
}
