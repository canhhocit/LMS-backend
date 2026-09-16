package com.ex.learninghub.common.queue;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class AsyncTaskQueueService {

    @Data
    @Builder
    public static class QueueJob {
        private String jobId;
        private String type; // MASS_MAIL, EXAM_SUBMISSION_BATCH, REPORT_GEN
        private String payload;
        private String status; // PENDING, PROCESSING, COMPLETED
    }

    private final ConcurrentLinkedQueue<QueueJob> jobQueue = new ConcurrentLinkedQueue<>();

    public QueueJob enqueueJob(String type, String payload) {
        String id = "job_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        QueueJob job = QueueJob.builder()
                .jobId(id)
                .type(type)
                .payload(payload)
                .status("PENDING")
                .build();

        jobQueue.offer(job);
        log.info("Đã đẩy tác vụ bất đồng bộ vào Hàng đợi Queue [ID: {}, Type: {}]. Số lượng trong hàng đợi: {}", id, type, jobQueue.size());
        return job;
    }

    public int getQueueSize() {
        return jobQueue.size();
    }
}
