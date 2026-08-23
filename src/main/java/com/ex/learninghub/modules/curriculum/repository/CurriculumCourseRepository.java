package com.ex.learninghub.modules.curriculum.repository;

import com.ex.learninghub.modules.curriculum.entity.CurriculumCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumCourseRepository extends JpaRepository<CurriculumCourse, Long> {
    List<CurriculumCourse> findByCurriculumId(Long curriculumId);
    Optional<CurriculumCourse> findByCurriculumIdAndCourseId(Long curriculumId, Long courseId);
    void deleteByCurriculumIdAndCourseId(Long curriculumId, Long courseId);
}
