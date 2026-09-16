package com.ex.learninghub.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SlidingWindowRateLimiterTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    private SlidingWindowRateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new SlidingWindowRateLimiterService(redisTemplate);
    }

    @Test
    @DisplayName("Nên cho phép request trong ngưỡng giới hạn Sliding Window")
    void isAllowed_WithinLimit() {
        boolean allowed = rateLimiterService.isAllowed("user_100", 5, 60);

        assertThat(allowed).isTrue();
    }
}
