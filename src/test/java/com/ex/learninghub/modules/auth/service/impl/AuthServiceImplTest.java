package com.ex.learninghub.modules.auth.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.security.JwtTokenProvider;
import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.repository.RefreshTokenRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("student@test.edu.vn")
                .fullName("Test Student")
                .role(Role.STUDENT)
                .password("encoded-password")
                .build();
    }

    @Test
    void login_withValidCredentials_returnsToken() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("student@test.edu.vn");
        request.setPassword("Password@123");

        when(userRepository.findByEmail("student@test.edu.vn")).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken(anyString())).thenReturn("jwt-token");
        when(refreshTokenRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(inv -> inv.getArgument(0));

        var response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("student@test.edu.vn");
        assertThat(response.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    void login_withUnknownUser_throwsUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("unknown@test.edu.vn");
        request.setPassword("Password@123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByStudentCode(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByLecturerCode(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AppException.class);
    }
}