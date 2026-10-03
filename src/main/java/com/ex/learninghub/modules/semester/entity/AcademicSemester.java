package com.ex.learninghub.modules.semester.entity;

import com.ex.learninghub.modules.semester.entity.SemesterStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "academic_semesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicSemester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mã định danh duy nhất: "2026-2027-1" */
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    /** Tên hiển thị: "Học kỳ 1 năm học 2026-2027" */
    @Column(nullable = false, length = 150)
    private String name;

    /** Năm học: "2026-2027" */
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    /** Học kỳ thứ mấy trong năm học (1, 2 hoặc 3 cho kỳ hè) */
    @Column(name = "semester_no", nullable = false)
    private Integer semesterNo;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SemesterStatus status = SemesterStatus.UPCOMING;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** Tiện ích: chuỗi hiển thị ngắn gọn, e.g. "HK1 2026-2027" */
    @Transient
    public String getShortLabel() {
        return "HK" + semesterNo + " " + academicYear;
    }
}
