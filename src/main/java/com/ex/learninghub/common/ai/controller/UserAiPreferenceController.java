package com.ex.learninghub.common.ai.controller;

import com.ex.learninghub.common.ai.dto.UserAiPreferenceRequest;
import com.ex.learninghub.common.ai.dto.UserAiPreferenceResponse;
import com.ex.learninghub.common.ai.service.UserAiPreferenceService;
import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/preferences")
@RequiredArgsConstructor
@Tag(name = "Spring AI Personalization & Preferences", description = "API quản lý tùy chọn cá nhân hóa AI của người dùng (Xưng hô, Giọng điệu, Độ dài phản hồi, Ngữ cảnh)")
@SecurityRequirement(name = "bearerAuth")
public class UserAiPreferenceController {

    private final UserAiPreferenceService userAiPreferenceService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Xem tùy chọn cá nhân hóa AI của tôi", description = "Lấy các thiết lập cá nhân hóa AI (cách xưng hô, giọng điệu, độ dài câu trả lời, ngữ cảnh cá nhân) của tài khoản hiện tại.")
    public ResponseEntity<ApiResponse<UserAiPreferenceResponse>> getMyAiPreference(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserAiPreferenceResponse response = userAiPreferenceService.getMyAiPreference(principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cập nhật tùy chọn cá nhân hóa AI của tôi", description = "Cho phép người dùng tùy chỉnh cách AI xưng hô, giọng điệu phản hồi, độ dài bài trả lời và ngữ cảnh cá nhân.")
    public ResponseEntity<ApiResponse<UserAiPreferenceResponse>> updateMyAiPreference(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserAiPreferenceRequest request
    ) {
        UserAiPreferenceResponse response = userAiPreferenceService.updateMyAiPreference(principal, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
