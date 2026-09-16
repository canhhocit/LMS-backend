package com.ex.learninghub.modules.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAdvisorResponse {

    private Long studentId;
    private String studentName;
    private Double gpa;
    private String academicStatus;
    private String learningStyle;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;
    private List<StudyPlanStep> studyPlan;
    private String aiAdviceSummary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyPlanStep {
        private Integer stepOrder;
        private String actionTitle;
        private String description;
        private String timeframe;
    }
}
