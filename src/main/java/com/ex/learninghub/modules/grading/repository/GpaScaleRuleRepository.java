package com.ex.learninghub.modules.grading.repository;

import com.ex.learninghub.modules.grading.entity.GpaScaleRule;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface GpaScaleRuleRepository extends JpaRepository<GpaScaleRule, Long> {
    List<GpaScaleRule> findByCurriculumIdOrderBySortOrderAsc(Long curriculumId);
    void deleteByCurriculumId(Long curriculumId);
}
