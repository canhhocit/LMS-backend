package com.ex.learninghub.common.config;

import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeepAliveScheduler {

    private final UserRepository userRepository;

    // Ping Neon DB every 4 minutes (240,000 ms) while app is active
    @Scheduled(fixedRate = 240000)
    public void pingDatabase() {
        try {
            long count = userRepository.count();
            log.info("[KeepAliveScheduler] Pinged Neon DB successfully. User count: {}", count);
        } catch (Exception e) {
            log.warn("[KeepAliveScheduler] Failed to ping database: {}", e.getMessage());
        }
    }
}
