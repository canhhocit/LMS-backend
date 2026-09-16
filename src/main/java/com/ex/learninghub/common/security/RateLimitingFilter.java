package com.ex.learninghub.common.security;

import com.ex.learninghub.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    // Fallback in‑memory lock for rare cases where Redis is unavailable
    private final Map<String, RateLimiter> inMemoryLimiters = new ConcurrentHashMap<>();
    // Redis template for distributed counters
    private final StringRedisTemplate redisTemplate;

    
    public RateLimitingFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Định nghĩa rules cho từng endpoint
    private static final Map<String, RateLimitRule> RULES = Map.of(
            "/auth/login", new RateLimitRule(5, Duration.ofMinutes(1)),         // 5 lần/phút
            "/auth/forgot-password", new RateLimitRule(3, Duration.ofHours(1)), // 3 lần/giờ
            "/auth/reset-password", new RateLimitRule(5, Duration.ofHours(1)),  // 5 lần/giờ
            "/auth/refresh", new RateLimitRule(20, Duration.ofMinutes(1))       // 20 lần/phút
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
                ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 1024 * 1024);
        String path = wrappedRequest.getRequestURI();
        String normalizedPath = path.replaceFirst("^/api/v1", "");
        RateLimitRule rule = RULES.get(normalizedPath);
        if (rule == null) {
            filterChain.doFilter(wrappedRequest, response);
            return;
        }
        String key = buildKey(wrappedRequest, normalizedPath);
        if (key == null) {
            log.warn("Cannot build rate limit key for {} from IP {}", normalizedPath, resolveClientIp(wrappedRequest));
            filterChain.doFilter(wrappedRequest, response);
            return;
        }
        // Try Redis first
        Boolean allowed = tryRedisRateLimit(key, rule);
        if (allowed == null) { // Redis unavailable, fallback to in‑memory
            RateLimiter limiter = inMemoryLimiters.computeIfAbsent(key, k -> createRateLimiter(rule));
            allowed = limiter.acquirePermission();
        }
        if (allowed) {
            filterChain.doFilter(wrappedRequest, response);
        } else {
            log.warn("Rate limit exceeded for key: {}", key);
            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ApiResponse<Object> body = ApiResponse.builder()
                    .code(4290)
                    .message("Too many requests. Please try again later.")
                    .build();
            response.getWriter().write(objectMapper.writeValueAsString(body));
        }
    }

    /**
     * Use Redis INCR with expiry to enforce the limit. Returns null if Redis throws an exception.
     */
    private Boolean tryRedisRateLimit(String key, RateLimitRule rule) {
        try {
            Long current = redisTemplate.opsForValue().increment(key);
            if (current == null) return false;
            if (current == 1) {
                // Set TTL on first hit
                redisTemplate.expire(key, rule.window());
            }
            return current <= rule.capacity();
        } catch (Exception e) {
            log.debug("Redis rate limiter failed, falling back to in‑memory: {}", e.getMessage());
            return null;
        }
    }

    private String buildKey(HttpServletRequest request, String path) {
        String ip = resolveClientIp(request);
        if ("/auth/login".equals(path)) {
            String identifier = extractIdentifierFromRequest(request);
            if (identifier == null) {
                return path + ":" + ip;
            }
            return path + ":" + ip + ":" + identifier.toLowerCase().trim();
        }
        return path + ":" + ip;
    }

    private String extractIdentifierFromRequest(HttpServletRequest request) {
        try {
            if (!(request instanceof ContentCachingRequestWrapper wrapper)) {
                return null;
            }
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length == 0) {
                content = wrapper.getInputStream().readAllBytes();
            }
            if (content.length == 0) return null;
            String encoding = request.getCharacterEncoding();
            String body = new String(content, encoding != null ? encoding : "UTF-8");
            @SuppressWarnings("unchecked")
            Map<String, Object> json = objectMapper.readValue(body, Map.class);
            Object identifier = json.get("identifier");
            if (identifier == null) identifier = json.get("email");
            if (identifier == null) identifier = json.get("username");
            return identifier != null ? identifier.toString() : null;
        } catch (Exception e) {
            log.debug("Failed to parse request body for rate limiting: {}", e.getMessage());
            return null;
        }
    }

    private RateLimiter createRateLimiter(RateLimitRule rule) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(rule.capacity())
                .limitRefreshPeriod(rule.window())
                .timeoutDuration(Duration.ofSeconds(0))
                .build();
        return RateLimiter.of("rate-limiter-" + System.currentTimeMillis(), config);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private record RateLimitRule(int capacity, Duration window) {}
}
