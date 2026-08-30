package com.ex.learninghub.modules.grading.repository;

import com.ex.learninghub.modules.grading.entity.GpaScaleRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpaScaleRuleRepository extends JpaRepository<GpaScaleRule, Long> {
    List<GpaScaleRule> findByCurriculumIdOrderBySortOrderAsc(Long curriculumId);
    void deleteByCurriculumId(Long curriculumId);
}
