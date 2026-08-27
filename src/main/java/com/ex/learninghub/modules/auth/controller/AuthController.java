package com.ex.learninghub.modules.auth.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.auth.dto.request.ChangePasswordRequest;
import com.ex.learninghub.modules.auth.dto.request.ForgotPasswordRequest;
import com.ex.learninghub.modules.auth.dto.request.LoginRequest;
import com.ex.learninghub.modules.auth.dto.request.RefreshTokenRequest;
import com.ex.learninghub.modules.auth.dto.request.ResetPasswordRequest;
import com.ex.learninghub.modules.auth.dto.response.AuthResponse;
import com.ex.learninghub.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Xác thực & Phân quyền", description = "Các API liên quan đến đăng nhập, đăng xuất, refresh token, đổi mật khẩu và khôi phục mật khẩu")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Đăng nhập hệ thống",
            description = "Xác thực người dùng bằng username/email và mật khẩu. Trả về access token (JWT) và refresh token nếu thành công."
    )
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Đổi mật khẩu",
            description = "Cho phép người dùng đã đăng nhập đổi mật khẩu. Cần cung cấp mật khẩu hiện tại và mật khẩu mới."
    )
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userPrincipal.getUsername(), request);
        return ApiResponse.success(null);
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Quên mật khẩu - Yêu cầu đặt lại",
            description = "Gửi email chứa link/otp đặt lại mật khẩu đến địa chỉ email đã đăng ký của người dùng."
    )
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Đặt lại mật khẩu bằng token",
            description = "Đặt lại mật khẩu mới khi người dùng cung cấp token hợp lệ nhận được từ email quên mật khẩu."
    )
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Làm mới access token",
            description = "Sử dụng refresh token để lấy access token mới khi access token cũ đã hết hạn."
    )
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request.getRefreshToken()));
    }
}
