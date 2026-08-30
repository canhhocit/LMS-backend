package com.ex.learninghub.modules.grading.dto.response;

import com.ex.learninghub.modules.grading.entity.GpaScaleRule;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpaScaleRuleResponse {
    private Long id;
    private Long curriculumId;
    private BigDecimal minScore10;
    private BigDecimal gpa4;
    private Integer sortOrder;

    public static GpaScaleRuleResponse from(GpaScaleRule rule) {
        if (rule == null) return null;
        return GpaScaleRuleResponse.builder()
                .id(rule.getId())
                .curriculumId(rule.getCurriculum() != null ? rule.getCurriculum().getId() : null)
                .minScore10(rule.getMinScore10())
                .gpa4(rule.getGpa4())
                .sortOrder(rule.getSortOrder())
                .build();
    }
}
