package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.InVideoQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InVideoQuizRepository extends JpaRepository<InVideoQuiz, Long> {
    List<InVideoQuiz> findByLessonIdOrderByTriggerAtSecondsAsc(Long lessonId);
}
