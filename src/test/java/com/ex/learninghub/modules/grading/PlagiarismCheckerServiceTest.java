package com.ex.learninghub.modules.grading;

import com.ex.learninghub.modules.grading.service.PlagiarismCheckerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlagiarismCheckerServiceTest {

    private PlagiarismCheckerService plagiarismCheckerService;

    @BeforeEach
    void setUp() {
        plagiarismCheckerService = new PlagiarismCheckerService();
    }

    @Test
    @DisplayName("Nên tính toán % giống nhau bài nộp chính xác")
    void compareSubmissions_HighSimilarity() {
        String text1 = "Hệ thống quản lý học tập LearningHub xây dựng bằng Spring Boot và ReactJS";
        String text2 = "Hệ thống quản lý học tập LearningHub xây dựng bằng Spring Boot và VueJS";

        PlagiarismCheckerService.PlagiarismReport report = plagiarismCheckerService.compareSubmissions(
                1L, "Nguyễn Văn A", text1,
                2L, "Trần Thị B", text2
        );

        assertThat(report).isNotNull();
        assertThat(report.getSimilarityPercentage()).isGreaterThan(70.0);
        assertThat(report.getRiskLevel()).isIn("HIGH", "CRITICAL");
    }
}
