package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByChapterId(Long chapterId);
}
