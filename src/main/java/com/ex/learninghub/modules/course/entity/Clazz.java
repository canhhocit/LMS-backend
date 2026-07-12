package com.ex.learninghub.modules.course.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.user.entity.User;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "classes")
public class Clazz extends BaseEntity {

    @Column(name = "class_code", unique = true, nullable = false, length = 50)
    private String classCode;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(length = 50)
    private String semester;

    @Column(name = "academic_year", length = 50)
    private String academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private User lecturer;
}
