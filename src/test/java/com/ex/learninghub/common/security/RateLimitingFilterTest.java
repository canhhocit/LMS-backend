package com.ex.learninghub.common.security;

import com.ex.learninghub.modules.auth.controller.AuthController;
import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.service.AuthService;
import com.ex.learninghub.common.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, RateLimitingFilter.class})
public class RateLimitingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private AuthService authService;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setIdentifier("test@example.com");
        loginRequest.setPassword("wrongpassword");
        // Mock authentication failure for login attempts
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
    }

    @Test
    void testRateLimiting_LoginEndpoint_Returns429After5Attempts() throws Exception {
        String url = "/auth/login";
        String body = objectMapper.writeValueAsString(loginRequest);

        // Gửi 5 request đầu, mong đợi 401 (vì sai mật khẩu)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isUnauthorized());
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
