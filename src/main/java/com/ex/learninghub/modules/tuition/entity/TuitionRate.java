package com.ex.learninghub.modules.tuition.entity;

import com.ex.learninghub.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tuition_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionRate extends BaseEntity {

    /** Năm học áp dụng, VD: 2025-2026. */
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    /** Null nghĩa là mức giá mặc định áp dụng cho mọi học kỳ trong năm học. */
    @Column(length = 20)
    private String semester;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    /** Đơn giá mỗi tín chỉ (VNĐ). */
    @Column(name = "price_per_credit", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerCredit;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
