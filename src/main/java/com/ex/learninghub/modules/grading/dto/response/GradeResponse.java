package com.ex.learninghub.modules.grading.dto.response;

import com.ex.learninghub.modules.grading.entity.Grade;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GradeResponse {

    private Long id;
    private Long classId;
    private Long studentId;
    private String studentName;
    private BigDecimal midtermScore;
    private BigDecimal finalScore;
    private BigDecimal totalScore;

    public static GradeResponse from(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .classId(grade.getClazz() != null ? grade.getClazz().getId() : null)
                .studentId(grade.getStudent() != null ? grade.getStudent().getId() : null)
                .studentName(grade.getStudent() != null ? grade.getStudent().getFullName() : null)
                .midtermScore(grade.getMidtermScore())
                .finalScore(grade.getFinalScore())
                .totalScore(grade.getTotalScore())
                .build();
    }
}
