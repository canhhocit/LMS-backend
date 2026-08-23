package com.ex.learninghub.modules.curriculum.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curriculum_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumCourse extends BaseEntity {

    @Column(name = "curriculum_id", nullable = false)
    private Long curriculumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", insertable = false, updatable = false)
    private Curriculum curriculum;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @Column(name = "semester_no", nullable = false)
    private Integer semesterNo;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = true;
}
