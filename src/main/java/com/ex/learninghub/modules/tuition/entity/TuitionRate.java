package com.ex.learninghub.modules.tuition.entity;

import com.ex.learninghub.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tuition_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionRate extends BaseEntity {

    /** Năm học áp dụng, VD: 2025-2026 (unique). */
    @Column(name = "academic_year", nullable = false, length = 20, unique = true)
    private String academicYear;

    /** Đơn giá mỗi tín chỉ (VNĐ). */
    @Column(name = "price_per_credit", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerCredit;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
