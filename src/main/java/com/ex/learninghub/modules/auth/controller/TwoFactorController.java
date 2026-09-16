package com.ex.learninghub.modules.auth.controller;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.TotpService;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.auth.dto.request.TwoFactorVerifyRequest;
import com.ex.learninghub.modules.auth.dto.response.TwoFactorSetupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/auth/2fa")
@RequiredArgsConstructor
@Tag(name = "Xác thực 2 Yếu tố (2FA / TOTP)", description = "Các API thiết lập và xác thực 2FA cho Google Authenticator")
public class TwoFactorController {

    private final TotpService totpService;

    @PostMapping("/setup")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Khởi tạo thiết lập 2FA",
            description = "Tạo Secret Key và URI mã QR cho ứng dụng Google Authenticator / Authy."
    )
    public ApiResponse<TwoFactorSetupResponse> setupTwoFactor(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String secretKey = totpService.generateSecretKey();
        String qrCodeUri = totpService.getQrCodeUri(secretKey, userPrincipal.getUsername());
        String qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" +
                URLEncoder.encode(qrCodeUri, StandardCharsets.UTF_8);

        return ApiResponse.success(TwoFactorSetupResponse.builder()
                .secretKey(secretKey)
                .qrCodeUri(qrCodeUri)
                .qrImageUrl(qrImageUrl)
                .build());
    }

    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Xác thực mã 2FA TOTP",
            description = "Kiểm tra mã 6 chữ số từ Google Authenticator với secret key đã được cấp."
    )
    public ApiResponse<Boolean> verifyTwoFactor(@Valid @RequestBody TwoFactorVerifyRequest request) {
        boolean isValid = totpService.verifyTotpCode(request.getSecretKey(), request.getCode());
        if (!isValid) {
            throw new AppException(ErrorCode.KEY_INVALID);
        }
        return ApiResponse.success(true);
    }
}
