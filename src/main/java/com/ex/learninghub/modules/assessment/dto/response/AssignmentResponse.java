package com.ex.learninghub.modules.assessment.dto.response;

import com.ex.learninghub.modules.assessment.entity.Assignment;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AssignmentResponse {

    private Long id;
    private Long classId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private BigDecimal maxScore;
    private LocalDateTime createdAt;

    public static AssignmentResponse from(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .classId(assignment.getClazz() != null ? assignment.getClazz().getId() : null)
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .maxScore(assignment.getMaxScore())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}
