package com.ex.learninghub.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt_blacklist:";

    public void blacklistToken(String token, long ttlInMillis) {
        if (token == null || token.isBlank()) return;
        String redisKey = BLACKLIST_PREFIX + token;
        try {
            long ttlSeconds = Math.max(ttlInMillis / 1000, 60); // Default min 60 seconds
            redisTemplate.opsForValue().set(redisKey, "revoked", Duration.ofSeconds(ttlSeconds));
            log.info("Token đã được đưa vào blacklist thành công: {} (TTL {}s)", redisKey, ttlSeconds);
        } catch (Exception e) {
            log.warn("Không thể lưu token blacklist vào Redis: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) return false;
        String redisKey = BLACKLIST_PREFIX + token;
        try {
            Boolean hasKey = redisTemplate.hasKey(redisKey);
            return Boolean.TRUE.equals(hasKey);
        } catch (Exception e) {
            log.warn("Đọc Redis blacklist thất bại: {}", e.getMessage());
            return false;
        }
    }
}
