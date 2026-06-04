package com.ex.learninghub.modules.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.enums.UserStatus;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.JwtTokenProvider;
import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.dto.request.RegisterRequest;
import com.ex.learninghub.modules.auth.dto.response.AuthResponse;
import com.ex.learninghub.modules.auth.service.AuthService;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.mapper.UserMapper;
import com.ex.learninghub.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.LEARNER)
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail());
        UserResponse userResponse = userMapper.toUserResponse(user);

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        UserResponse userResponse = userMapper.toUserResponse(user);

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }
}
