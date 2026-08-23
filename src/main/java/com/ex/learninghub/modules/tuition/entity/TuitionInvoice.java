package com.ex.learninghub.modules.tuition.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tuition_invoices",
        uniqueConstraints = @UniqueConstraint(name = "uk_tuition_invoice",
                columnNames = {"student_id", "semester", "academic_year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionInvoice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, length = 20)
    private String semester;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "total_credits", nullable = false)
    private Integer totalCredits;

    @Column(name = "price_per_credit", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerCredit;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    /** UNPAID | PAID */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "UNPAID";

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
