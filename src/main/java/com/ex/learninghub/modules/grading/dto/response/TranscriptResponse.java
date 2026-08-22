package com.ex.learninghub.modules.grading.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptResponse {
    private String courseCode;
    private String courseTitle;
    private Integer credit;
    private BigDecimal totalScore;
    private Double gpa;
}
