package com.ex.learninghub.modules.grading.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class PlagiarismCheckerService {

    @Data
    @Builder
    public static class PlagiarismReport {
        private Long submissionId1;
        private Long submissionId2;
        private String student1Name;
        private String student2Name;
        private double similarityPercentage;
        private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    }

    public PlagiarismReport compareSubmissions(Long id1, String name1, String text1, Long id2, String name2, String text2) {
        double similarity = calculateJaccardSimilarity(text1, text2);
        double percentage = Math.round(similarity * 100.0 * 100.0) / 100.0;

        String risk = "LOW";
        if (percentage >= 80.0) risk = "CRITICAL";
        else if (percentage >= 50.0) risk = "HIGH";
        else if (percentage >= 30.0) risk = "MEDIUM";

        log.info("Kết quả kiểm tra sao chép giữa bài {} và bài {}: {}% (Risk: {})", id1, id2, percentage, risk);

        return PlagiarismReport.builder()
                .submissionId1(id1)
                .submissionId2(id2)
                .student1Name(name1)
                .student2Name(name2)
                .similarityPercentage(percentage)
                .riskLevel(risk)
                .build();
    }

    private double calculateJaccardSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null || s1.isBlank() || s2.isBlank()) return 0.0;

        Set<String> set1 = new HashSet<>(Arrays.asList(s1.toLowerCase().split("\\s+")));
        Set<String> set2 = new HashSet<>(Arrays.asList(s2.toLowerCase().split("\\s+")));

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }
}
