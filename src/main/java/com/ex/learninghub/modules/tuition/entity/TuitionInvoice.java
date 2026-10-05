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

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    @Column(name = "payos_order_code", unique = true)
    private Long payosOrderCode;

    @Column(name = "payos_checkout_url", length = 2048)
    private String payosCheckoutUrl;

    @Column(name = "payos_qr_code", columnDefinition = "TEXT")
    private String payosQrCode;

    @Column(name = "payos_account_name", length = 255)
    private String payosAccountName;

    @Column(name = "payos_account_number", length = 100)
    private String payosAccountNumber;

    @Column(name = "payos_bank_name", length = 255)
    private String payosBankName;

    @Column(name = "payos_description", length = 255)
    private String payosDescription;
}
