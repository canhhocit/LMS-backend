package com.ex.learninghub.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlidingWindowRateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final ConcurrentHashMap<String, AtomicInteger> localCounter = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, int maxRequests, long windowSeconds) {
        try {
            long now = System.currentTimeMillis();
            long windowStart = now - (windowSeconds * 1000);
            String redisKey = "rate_limit:sliding:" + key;

            // Remove expired entries
            redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);

            // Count current requests in window
            Long count = redisTemplate.opsForZSet().zCard(redisKey);
            if (count != null && count >= maxRequests) {
                log.warn("WAF Rate Limiter: Khóa {} đã vượt quá giới hạn {} request/{}s", key, maxRequests, windowSeconds);
                return false;
            }

            // Add current timestamp
            redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), (double) now);
            return true;
        } catch (Exception e) {
            log.warn("Redis không khả dụng, sử dụng In-Memory Sliding Window Rate Limiter fallback: {}", e.getMessage());
            AtomicInteger count = localCounter.computeIfAbsent(key, k -> new AtomicInteger(0));
            if (count.incrementAndGet() > maxRequests) {
                return false;
            }
            return true;
        }
    }
}
