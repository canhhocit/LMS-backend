package com.ex.learninghub.modules.assessment.dto.response;

import com.ex.learninghub.common.enums.SubmissionType;
import com.ex.learninghub.modules.assessment.entity.Submission;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SubmissionResponse {

    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private SubmissionType submissionType;
    private String fileUrl;
    private List<String> fileUrls;
    private String externalLink;
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
                .submissionType(submission.getSubmissionType())
                .fileUrl(submission.getFileUrl())
                .fileUrls(submission.getFileUrls() == null || submission.getFileUrls().isBlank() ? List.of() : Arrays.stream(submission.getFileUrls().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()))
                .externalLink(submission.getExternalLink())
                .submittedAt(submission.getSubmittedAt())
                .isLate(submission.getIsLate())
                .score(submission.getScore())
                .feedback(submission.getFeedback())
                .build();
    }
}
