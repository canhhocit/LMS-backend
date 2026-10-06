package com.ex.learninghub.modules.tuition.dto.response;

import com.ex.learninghub.modules.tuition.entity.TuitionRate;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionRateResponse {
    private Long id;
    private String academicYear;
    private String semester;
    private LocalDate effectiveFrom;
    private BigDecimal pricePerCredit;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TuitionRateResponse from(TuitionRate r) {
        return TuitionRateResponse.builder()
                .id(r.getId())
                .academicYear(r.getAcademicYear())
                .semester(r.getSemester())
                .effectiveFrom(r.getEffectiveFrom())
                .pricePerCredit(r.getPricePerCredit())
                .isActive(r.getIsActive())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
