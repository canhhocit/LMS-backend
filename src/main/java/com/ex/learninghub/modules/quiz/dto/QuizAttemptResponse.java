package com.ex.learninghub.modules.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponse {
    private Long quizId;
    private int totalQuestions;
    private int correctAnswers;
    private BigDecimal score;
    private BigDecimal totalScore;
}