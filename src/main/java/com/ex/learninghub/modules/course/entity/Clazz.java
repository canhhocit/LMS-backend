package com.ex.learninghub.modules.course.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.semester.entity.AcademicSemester;
import com.ex.learninghub.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    /**
     * @deprecated Dùng {@link #academicSemester} thay thế.
     * Vẫn giữ để tương thích với dữ liệu cũ và các query legacy.
     */
    @Deprecated
    @Column(length = 20)
    private String semester;

    /**
     * @deprecated Dùng {@link #academicSemester} thay thế.
     */
    @Deprecated
    @Column(name = "academic_year", length = 20)
    private String academicYear;

    /** Học kỳ chính thức của lớp — nguồn dữ liệu chuẩn duy nhất. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", foreignKey = @ForeignKey(name = "FK_CLASS_SEMESTER"))
    private AcademicSemester academicSemester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", foreignKey = @ForeignKey(name = "FK_CLASS_COURSE"))
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", foreignKey = @ForeignKey(name = "FK_CLASS_LECTURER"))
    private User lecturer;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "is_grade_locked")
    @Builder.Default
    private Boolean isGradeLocked = false;

    
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "exam_date")
    private LocalDateTime examDate;

    @Column(name = "exam_room", length = 50)
    private String examRoom;

    @Column(name = "exam_format", length = 50)
    private String examFormat;

    @Column(name = "exam_duration")
    private Integer examDuration;

    /** Tiện ích: lấy semester string từ FK hoặc fallback sang string cũ */
    @Transient
    public String getEffectiveSemester() {
        if (academicSemester != null) return "HK" + academicSemester.getSemesterNo();
        return semester;
    }

    @Transient
    public String getEffectiveAcademicYear() {
        if (academicSemester != null) return academicSemester.getAcademicYear();
        return academicYear;
    }
}