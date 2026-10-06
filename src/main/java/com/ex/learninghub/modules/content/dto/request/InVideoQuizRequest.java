package com.ex.learninghub.modules.content.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record InVideoQuizRequest(
        @NotNull Long lessonId,
        @NotNull @DecimalMin("0.0") BigDecimal triggerAtSeconds,
        @NotBlank String questionText,
        @NotBlank String optionA,
        @NotBlank String optionB,
        String optionC,
        String optionD,
        @NotBlank @Pattern(regexp = "[A-D]") String correctOption
) {
}
