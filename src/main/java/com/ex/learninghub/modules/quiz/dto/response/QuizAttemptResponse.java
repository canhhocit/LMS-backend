package com.ex.learninghub.modules.quiz.dto.response;

import com.ex.learninghub.modules.assessment.entity.QuizAttempt;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class QuizAttemptResponse {

    private Long attemptId;
    private Long quizId;
    private Long studentId;
    private int totalQuestions;
    private int correctAnswers;
    private BigDecimal score;
    private BigDecimal totalScore;
    private LocalDateTime submittedAt;

    public static QuizAttemptResponse from(QuizAttempt attempt, int totalQuestions, int correctAnswers,
                                           BigDecimal totalScore) {
        return QuizAttemptResponse.builder()
                .attemptId(attempt.getId())
                .quizId(attempt.getQuiz() != null ? attempt.getQuiz().getId() : null)
                .studentId(attempt.getStudent() != null ? attempt.getStudent().getId() : null)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .score(attempt.getScore())
                .totalScore(totalScore)
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }
}
