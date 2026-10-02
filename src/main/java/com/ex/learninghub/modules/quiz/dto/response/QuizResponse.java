package com.ex.learninghub.modules.quiz.dto.response;

import com.ex.learninghub.modules.assessment.entity.Quiz;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class QuizResponse {

    private Long id;
    private Long classId;
    private String title;
    private Integer durationMinutes;
    private BigDecimal totalScore;
    private LocalDateTime createdAt;
    private QuizAttemptResponse myAttempt;

    public static QuizResponse from(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .classId(quiz.getClazz() != null ? quiz.getClazz().getId() : null)
                .title(quiz.getTitle())
                .durationMinutes(quiz.getDurationMinutes())
                .totalScore(quiz.getTotalScore())
                .createdAt(quiz.getCreatedAt())
                .build();
    }
}
