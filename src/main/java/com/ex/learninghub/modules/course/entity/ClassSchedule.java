package com.ex.learninghub.modules.course.entity;

import com.ex.learninghub.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "class_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSchedule extends BaseEntity {

    @Column(name = "clazz_id", nullable = false)
    private Long clazzId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clazz_id", insertable = false, updatable = false)
    private Clazz clazz;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 1=Monday, 7=Sunday

    @Column(name = "start_period", nullable = false)
    private Integer startPeriod;

    @Column(name = "end_period", nullable = false)
    private Integer endPeriod;

    @Column(length = 50)
    private String room;
}
