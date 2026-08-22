package com.ex.learninghub.modules.quiz.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class QuizRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @Positive(message = "Total score must be positive")
    private BigDecimal totalScore;
}
