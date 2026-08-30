package com.ex.learninghub.modules.grading.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpaScaleRuleRequest {
    @NotNull
    private BigDecimal minScore10;
    @NotNull
    private BigDecimal gpa4;
    @NotNull
    private Integer sortOrder;
}
