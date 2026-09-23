package com.ex.learninghub.modules.assessment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiGradingResponse {
    private Long submissionId;
    private String studentName;
    private String assignmentTitle;
    private BigDecimal suggestedScore;
    private BigDecimal maxScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private String detailedFeedback;
    private String summary;
}
