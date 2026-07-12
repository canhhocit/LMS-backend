package com.ex.learninghub.modules.auth.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.JwtTokenProvider;
import com.ex.learninghub.modules.auth.dto.request.ChangePasswordRequest;
import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.dto.response.AuthResponse;
import com.ex.learninghub.modules.auth.service.AuthService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest request) {
        // Handle login with email, student code or lecturer code
        User user = userRepository.findByEmail(request.getIdentifier())
                .orElseGet(() -> userRepository.findByStudentCode(request.getIdentifier())
                        .orElseGet(() -> userRepository.findByLecturerCode(request.getIdentifier())
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isFirstLogin(user.getIsFirstLogin() != null && user.getIsFirstLogin())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // or a specific password error
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setIsFirstLogin(false);
        userRepository.save(user);
    }
}
