package com.ex.learninghub.modules.assessment.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.course.entity.Clazz;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "quizzes")
public class Quiz extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Clazz clazz;

    @Column(nullable = false)
    private String title;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;
}
