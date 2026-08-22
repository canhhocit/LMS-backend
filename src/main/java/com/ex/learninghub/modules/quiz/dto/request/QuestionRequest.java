package com.ex.learninghub.modules.quiz.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    private String questionText;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @NotBlank(message = "Correct answer is required")
    @Pattern(regexp = "^[A-D]$", message = "Correct answer must be A, B, C or D")
    private String correctAnswer;
}
