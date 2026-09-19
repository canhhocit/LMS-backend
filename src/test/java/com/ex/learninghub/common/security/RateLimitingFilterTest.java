package com.ex.learninghub.common.security;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.exception.GlobalExceptionHandler;
import com.ex.learninghub.modules.auth.controller.AuthController;
import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.service.AuthService;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RateLimitingFilterTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        RateLimitingFilter rateLimitingFilter = new RateLimitingFilter(redisTemplate);
        AuthController authController = new AuthController(authService, tokenBlacklistService, userRepository);

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilters(rateLimitingFilter)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setIdentifier("test@example.com");
        loginRequest.setPassword("wrongpassword");
    }

    @Test
    void testRateLimiting_LoginEndpoint_Returns429After5Attempts() throws Exception {
        lenient().doThrow(new AppException(ErrorCode.INVALID_CREDENTIALS)).when(authService).login(any());

        String url = "/auth/login";
        String body = objectMapper.writeValueAsString(loginRequest);

        // Gửi 5 request đầu
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));
        }

        // Request thứ 6, mong đợi 429
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.code").value(4290))
                .andExpect(jsonPath("$.message").value("Too many requests. Please try again later."));
    }

    @Test
    void testRateLimiting_ForgotPasswordEndpoint_Returns429After3Attempts() throws Exception {
        String url = "/auth/forgot-password";
        String body = "{\"email\":\"test@example.com\"}";

        // Gửi 3 request đầu, mong đợi 200 (service sẽ không thực sự gửi email vì đã mock)
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isOk());
        }

        // Request thứ 4, mong đợi 429
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().is(429));
    }
}
