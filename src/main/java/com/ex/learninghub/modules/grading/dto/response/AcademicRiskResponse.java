package com.ex.learninghub.modules.grading.dto.response;

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
public class AcademicRiskResponse {

    private Long studentId;
    private String studentName;
    private Long classId;
    private String className;

    private long absentCount;
    private long totalSessions;
    private double absentRatio;

    private Double averageScore;

    private String riskLevel; // SAFE, WARNING, CRITICAL
    private String recommendationAction;
}
