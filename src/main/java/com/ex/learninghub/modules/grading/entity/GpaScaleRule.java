package com.ex.learninghub.modules.grading.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "gpa_scale_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpaScaleRule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    @Column(name = "min_score_10", nullable = false, precision = 4, scale = 2)
    private BigDecimal minScore10;

    @Column(name = "gpa_4", nullable = false, precision = 3, scale = 2)
    private BigDecimal gpa4;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
