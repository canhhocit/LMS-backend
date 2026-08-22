package com.ex.learninghub.modules.grading.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class GradeRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @DecimalMin(value = "0.0", message = "Midterm score must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Midterm score must be at most 10.0")
    private BigDecimal midtermScore;

    @DecimalMin(value = "0.0", message = "Final score must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Final score must be at most 10.0")
    private BigDecimal finalScore;
}
