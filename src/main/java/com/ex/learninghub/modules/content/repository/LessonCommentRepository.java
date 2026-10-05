package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.LessonComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonCommentRepository extends JpaRepository<LessonComment, Long> {
    List<LessonComment> findByLessonIdOrderByCreatedAtDesc(Long lessonId);
}