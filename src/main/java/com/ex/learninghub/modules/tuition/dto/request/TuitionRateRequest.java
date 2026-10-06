package com.ex.learninghub.modules.tuition.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionRateRequest {

    @NotBlank
    @Size(max = 20)
    private String academicYear;

    @Size(max = 20)
    private String semester;

    @NotNull
    private LocalDate effectiveFrom;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal pricePerCredit;

    private Boolean isActive;
}
