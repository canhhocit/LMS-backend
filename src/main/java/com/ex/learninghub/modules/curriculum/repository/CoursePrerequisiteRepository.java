package com.ex.learninghub.modules.curriculum.repository;

import com.ex.learninghub.modules.curriculum.entity.CoursePrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, Long> {
    List<CoursePrerequisite> findByCourseId(Long courseId);
    void deleteByCourseIdAndPrerequisiteCourseId(Long courseId, Long prereqId);
    boolean existsByCourseIdAndPrerequisiteCourseId(Long courseId, Long prereqId);
}
