package com.ex.learninghub.modules.course.entity;

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
@Table(name = "courses")
public class Course extends BaseEntity {

    @Column(name = "course_code", unique = true, nullable = false, length = 50)
    private String courseCode;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(nullable = false)
    private Integer credits;

    @Column(columnDefinition = "TEXT")
    private String description;
}
