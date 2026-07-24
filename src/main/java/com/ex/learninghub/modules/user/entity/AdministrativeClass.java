package com.ex.learninghub.modules.user.entity;

import com.ex.learninghub.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "administrative_classes")
public class AdministrativeClass extends BaseEntity {

    @Column(name = "class_name", unique = true, nullable = false, length = 50)
    private String className;

    @Column(length = 100)
    private String faculty;

    @Column(name = "academic_year", length = 20)
    private String academicYear;
}
