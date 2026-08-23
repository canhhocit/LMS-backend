package com.ex.learninghub.modules.grading.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicStatusResponse {
    /** GPA tích lũy toàn khóa (thang 4) */
    private BigDecimal cumulativeGpa;

    /** Tổng tín chỉ đã đăng ký (tín chỉ tích lũy) */
    private Integer totalCredits;

    /** Tổng tín chỉ đạt */
    private Integer passedCredits;

    /** Có đang bị cảnh báo học vụ hay không */
    private Boolean academicWarning;

    /** Mức cảnh báo (0 = none, 1 = warning, 2 = probation, 3 = severe) */
    private Integer warningLevel;

    /** Số môn đã học */
    private Integer totalCourses;

    /** Số môn đạt */
    private Integer passedCourses;

    /** Các môn bị trượt */
    private List<FailedCourse> failedCourses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailedCourse {
        private String courseCode;
        private String courseTitle;
        private Integer credit;
        private BigDecimal totalScore;
    }
}
