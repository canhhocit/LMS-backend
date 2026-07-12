package com.ex.learninghub.modules.grading.repository;

import com.ex.learninghub.modules.grading.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByClazzId(Long clazzId);
    List<Grade> findByStudentId(Long studentId);
    Optional<Grade> findByClazzIdAndStudentId(Long clazzId, Long studentId);
}
