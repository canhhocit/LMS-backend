package com.ex.learninghub.modules.grading.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "grading_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingPolicy extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false, unique = true)
    private Curriculum curriculum;

    @Column(name = "attendance_weight", nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal attendanceWeight = new BigDecimal("0.000");

    @Column(name = "midterm_weight", nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal midtermWeight = new BigDecimal("0.400");

    @Column(name = "final_weight", nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal finalWeight = new BigDecimal("0.600");
}
