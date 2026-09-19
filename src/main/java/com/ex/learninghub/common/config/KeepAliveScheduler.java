package com.ex.learninghub.common.config;

import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeepAliveScheduler {

    private final UserRepository userRepository;

    @Value("${app.backend-url:https://learninghub-6jdb.onrender.com}")
    private String backendUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Ping Neon DB & HTTP Self-Ping Render every 4 minutes (240,000 ms)
    // Giữ Neon DB (giới hạn 5 phút) & Render (giới hạn 15 phút) luôn 100% thức
    @Scheduled(fixedRate = 240000)
    public void keepAlive() {
        // 1. Direct DB Query để giữ Neon PostgreSQL luôn active
        try {
            long count = userRepository.count();
            log.info("[KeepAliveScheduler] Pinged Neon DB successfully. Active user count: {}", count);
        } catch (Exception e) {
            log.warn("[KeepAliveScheduler] Failed to ping Neon DB: {}", e.getMessage());
        }

        // 2. HTTP Self-Ping đến Render URL để hạ tầng Render ghi nhận Inbound Traffic
        try {
            String pingEndpoint = backendUrl.replaceAll("/+$", "") + "/api/v1/auth/ping";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pingEndpoint))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> log.info("[KeepAliveScheduler] Self-Ping HTTP status: {}", response.statusCode()))
                    .exceptionally(ex -> {
                        log.warn("[KeepAliveScheduler] Self-Ping HTTP failed: {}", ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.warn("[KeepAliveScheduler] Could not initiate HTTP self-ping: {}", e.getMessage());
        }
    }
}
