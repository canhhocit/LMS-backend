package com.ex.learninghub.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService(redisTemplate);
    }

    @Test
    void blacklistToken_savesKeyInRedisWithTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        tokenBlacklistService.blacklistToken("SAMPLE_REVOKED_TOKEN", 3600000L);

        verify(valueOperations).set(eq("jwt_blacklist:SAMPLE_REVOKED_TOKEN"), eq("revoked"), any(Duration.class));
    }

    @Test
    void isBlacklisted_returnsTrue_whenRedisHasKey() {
        when(redisTemplate.hasKey("jwt_blacklist:SAMPLE_REVOKED_TOKEN")).thenReturn(true);

        boolean isRevoked = tokenBlacklistService.isBlacklisted("SAMPLE_REVOKED_TOKEN");

        assertThat(isRevoked).isTrue();
    }
}
