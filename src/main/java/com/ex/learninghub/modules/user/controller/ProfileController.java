package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.user.dto.request.UpdateProfileRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(userService.getProfile(userPrincipal));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success(userService.updateProfile(userPrincipal, request));
    }
}
