package com.ex.learninghub.common.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncTaskQueueServiceTest {

    private AsyncTaskQueueService asyncTaskQueueService;

    @BeforeEach
    void setUp() {
        asyncTaskQueueService = new AsyncTaskQueueService();
    }

    @Test
    @DisplayName("Nên đẩy tác vụ vào hàng đợi thành công")
    void enqueueJob_Success() {
        AsyncTaskQueueService.QueueJob job = asyncTaskQueueService.enqueueJob("MASS_MAIL", "send_welcome_email");

        assertThat(job).isNotNull();
        assertThat(job.getStatus()).isEqualTo("PENDING");
        assertThat(asyncTaskQueueService.getQueueSize()).isEqualTo(1);
    }
}
