package com.ex.learninghub.modules.user.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "administrative_classes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AdministrativeClass extends BaseEntity {

    @Column(name = "class_name", unique = true, nullable = false, length = 50)
    private String className;

    @Column(length = 100)
    private String faculty;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;
}
