package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.user.dto.request.UpdateProfileRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@Tag(name = "Hồ sơ cá nhân", description = "Các API sinh viên/giảng viên xem và cập nhật hồ sơ cá nhân của mình")
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Xem hồ sơ cá nhân",
            description = "Lấy thông tin hồ sơ của người dùng hiện đang đăng nhập."
    )
    public ApiResponse<UserResponse> getProfile(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(userService.getProfile(userPrincipal));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Cập nhật hồ sơ cá nhân",
            description = "Cho phép người dùng hiện tại cập nhật thông tin cá nhân (họ tên, email, số điện thoại, ...)."
    )
    public ApiResponse<UserResponse> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success(userService.updateProfile(userPrincipal, request));
    }

    @PostMapping("/profile/avatar")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Upload avatar cá nhân",
            description = "Upload ảnh đại diện lên Cloudinary và lưu URL vào hồ sơ cá nhân của người dùng hiện tại."
    )
    public ApiResponse<UserResponse> uploadAvatar(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(userService.uploadAvatar(userPrincipal, file));
    }
}
