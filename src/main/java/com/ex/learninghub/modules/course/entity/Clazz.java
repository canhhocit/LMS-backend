package com.ex.learninghub.modules.course.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a class (Clazz) where students enroll.
 */
@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clazz extends BaseEntity {

    @Column(name = "class_code", nullable = false, length = 50)
    private String classCode;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @Column(length = 20)
    private String semester;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_CLASS_COURSE"))
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", foreignKey = @ForeignKey(name = "FK_CLASS_LECTURER"))
    private User lecturer;

    @Column(name = "max_students")
    private Integer maxStudents;
}