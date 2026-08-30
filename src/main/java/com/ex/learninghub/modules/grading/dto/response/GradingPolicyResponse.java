package com.ex.learninghub.modules.grading.dto.response;

import com.ex.learninghub.modules.grading.entity.GradingPolicy;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingPolicyResponse {
    private Long id;
    private Long curriculumId;
    private BigDecimal attendanceWeight;
    private BigDecimal midtermWeight;
    private BigDecimal finalWeight;

    public static GradingPolicyResponse from(GradingPolicy policy) {
        if (policy == null) return null;
        return GradingPolicyResponse.builder()
                .id(policy.getId())
                .curriculumId(policy.getCurriculum() != null ? policy.getCurriculum().getId() : null)
                .attendanceWeight(policy.getAttendanceWeight())
                .midtermWeight(policy.getMidtermWeight())
                .finalWeight(policy.getFinalWeight())
                .build();
    }
}
