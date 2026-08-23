package com.ex.learninghub.modules.tuition.dto.response;

import com.ex.learninghub.modules.tuition.entity.TuitionInvoice;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionInvoiceResponse {
    private Long id;
    private Long studentId;
    private String studentFullName;
    private String semester;
    private String academicYear;
    private Integer totalCredits;
    private BigDecimal pricePerCredit;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;

    public static TuitionInvoiceResponse from(TuitionInvoice i) {
        return TuitionInvoiceResponse.builder()
                .id(i.getId())
                .studentId(i.getStudent() != null ? i.getStudent().getId() : null)
                .studentFullName(i.getStudent() != null ? i.getStudent().getFullName() : null)
                .semester(i.getSemester())
                .academicYear(i.getAcademicYear())
                .totalCredits(i.getTotalCredits())
                .pricePerCredit(i.getPricePerCredit())
                .amount(i.getAmount())
                .status(i.getStatus())
                .paidAt(i.getPaidAt())
                .build();
    }
}
