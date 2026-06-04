package com.ex.learninghub.modules.auth.service;

import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.dto.request.RegisterRequest;
import com.ex.learninghub.modules.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
