package com.ex.learninghub.common.monitoring;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SystemMonitoringMetricsTest {

    private SystemMonitoringMetrics metrics;
    private SimpleMeterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new SystemMonitoringMetrics(registry);
    }

    @Test
    @DisplayName("Nên ghi nhận counter lượt đăng nhập chính xác")
    void incrementLoginCount_Success() {
        metrics.incrementLoginCount();
        metrics.incrementLoginCount();

        double count = registry.find("learninghub.login.success.count").counter().count();
        assertThat(count).isEqualTo(2.0);
    }
}
