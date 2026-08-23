package com.ex.learninghub.modules.tuition.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionRateRequest {

    @NotBlank
    @Size(max = 20)
    private String academicYear;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal pricePerCredit;

    private Boolean isActive;
}
