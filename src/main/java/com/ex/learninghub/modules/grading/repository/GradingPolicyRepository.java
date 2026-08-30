package com.ex.learninghub.modules.grading.repository;

import com.ex.learninghub.modules.grading.entity.GradingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradingPolicyRepository extends JpaRepository<GradingPolicy, Long> {
    Optional<GradingPolicy> findByCurriculumId(Long curriculumId);
}
