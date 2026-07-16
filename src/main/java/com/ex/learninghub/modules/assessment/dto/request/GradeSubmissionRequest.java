package com.ex.learninghub.modules.assessment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class GradeSubmissionRequest {

    @NotNull(message = "Score is required")
    private BigDecimal score;

    private String feedback;
}
