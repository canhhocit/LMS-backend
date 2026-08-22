package com.ex.learninghub.modules.assessment.dto.response;

import com.ex.learninghub.modules.assessment.entity.Submission;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionResponse {

    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private Boolean isLate;
    private BigDecimal score;
    private String feedback;

    public static SubmissionResponse from(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignment() != null ? submission.getAssignment().getId() : null)
                .studentId(submission.getStudent() != null ? submission.getStudent().getId() : null)
                .studentName(submission.getStudent() != null ? submission.getStudent().getFullName() : null)
                .fileUrl(submission.getFileUrl())
                .submittedAt(submission.getSubmittedAt())
                .isLate(submission.getIsLate())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .build();
    }
}
