package com.ex.learninghub.modules.auth.service;

import com.ex.learninghub.modules.auth.dto.request.ChangePasswordRequest;
import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    void changePassword(String email, ChangePasswordRequest request);
}
