package com.ex.learninghub.modules.assessment.repository;

import com.ex.learninghub.modules.assessment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClazzId(Long clazzId);
}
