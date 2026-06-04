package com.ex.learninghub.modules.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ex.learninghub.modules.course.entity.Chapter;
import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByCourseIdOrderBySortOrderAsc(Long courseId);
}
