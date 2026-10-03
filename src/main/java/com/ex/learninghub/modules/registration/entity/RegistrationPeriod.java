package com.ex.learninghub.modules.registration.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.semester.entity.AcademicSemester;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_periods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationPeriod extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    /** @deprecated Dùng {@link #academicSemester} thay thế */
    @Deprecated
    @Column(nullable = false, length = 20)
    private String semester;

    /** @deprecated Dùng {@link #academicSemester} thay thế */
    @Deprecated
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    /** Học kỳ mà đợt đăng ký này phục vụ — nguồn dữ liệu chuẩn duy nhất */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", foreignKey = @ForeignKey(name = "FK_REG_PERIOD_SEMESTER"))
    private AcademicSemester academicSemester;

    @Column(name = "open_at", nullable = false)
    private LocalDateTime openAt;

    @Column(name = "close_at", nullable = false)
    private LocalDateTime closeAt;

    @Column(name = "max_credits")
    private Integer maxCredits;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    /** Tiện ích: lấy semester string */
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

