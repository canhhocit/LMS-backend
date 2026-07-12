package com.ex.learninghub.modules.assessment.repository;

import com.ex.learninghub.modules.assessment.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByClazzId(Long clazzId);
}
