package com.ex.learninghub.modules.quiz.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuizAttemptRequest {

    @Valid
    @NotEmpty(message = "Answers are required")
    private List<Answer> answers;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Answer {

        @NotNull(message = "Question ID is required")
        private Long questionId;

        @NotBlank(message = "Selected answer is required")
        @Pattern(regexp = "^[A-D]$", message = "Selected answer must be A, B, C or D")
        private String selectedAnswer;
    }
}
