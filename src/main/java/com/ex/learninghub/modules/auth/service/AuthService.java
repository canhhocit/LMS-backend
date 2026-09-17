package com.ex.learninghub.modules.auth.service;

import com.ex.learninghub.modules.auth.dto.request.ChangePasswordRequest;
import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse googleLogin(com.ex.learninghub.modules.auth.dto.request.GoogleLoginRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    void forgotPassword(com.ex.learninghub.modules.auth.dto.request.ForgotPasswordRequest request);
    void resetPassword(com.ex.learninghub.modules.auth.dto.request.ResetPasswordRequest request);
    AuthResponse refresh(String refreshToken);
}
