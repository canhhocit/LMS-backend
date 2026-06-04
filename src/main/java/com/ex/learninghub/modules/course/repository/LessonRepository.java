package com.ex.learninghub.modules.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ex.learninghub.modules.course.entity.Lesson;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByChapterIdOrderBySortOrderAsc(Long chapterId);
}
