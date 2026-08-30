package com.ex.learninghub.modules.grading.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingPolicyRequest {
    @NotNull
    private BigDecimal attendanceWeight;
    @NotNull
    private BigDecimal midtermWeight;
    @NotNull
    private BigDecimal finalWeight;
}
