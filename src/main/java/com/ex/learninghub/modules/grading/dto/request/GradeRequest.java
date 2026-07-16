package com.ex.learninghub.modules.grading.dto.request;

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

    private BigDecimal midtermScore;

    private BigDecimal finalScore;
}
