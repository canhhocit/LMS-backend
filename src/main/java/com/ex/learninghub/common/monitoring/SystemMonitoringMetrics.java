package com.ex.learninghub.common.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SystemMonitoringMetrics {

    private final Counter loginSuccessCounter;
    private final Counter quizSubmissionCounter;

    public SystemMonitoringMetrics(MeterRegistry registry) {
        this.loginSuccessCounter = Counter.builder("learninghub.login.success.count")
                .description("Số lần đăng nhập thành công vào hệ thống LearningHub")
                .register(registry);

        this.quizSubmissionCounter = Counter.builder("learninghub.quiz.submission.count")
                .description("Số lượng bài thi trắc nghiệm đã nộp")
                .register(registry);
    }

    public void incrementLoginCount() {
        loginSuccessCounter.increment();
        log.debug("Đã ghi nhận +1 lượt đăng nhập thành công vào Micrometer metrics");
    }

    public void incrementQuizSubmissionCount() {
        quizSubmissionCounter.increment();
        log.debug("Đã ghi nhận +1 lượt nộp bài trắc nghiệm vào Micrometer metrics");
    }
}
