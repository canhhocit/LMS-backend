package com.ex.learninghub.modules.assessment.repository;

import com.ex.learninghub.modules.assessment.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByQuizId(Long quizId);
    List<QuizAttempt> findByStudentId(Long studentId);
    boolean existsByQuizIdAndStudentId(Long quizId, Long studentId);
    Optional<QuizAttempt> findByQuizIdAndStudentId(Long quizId, Long studentId);
}
