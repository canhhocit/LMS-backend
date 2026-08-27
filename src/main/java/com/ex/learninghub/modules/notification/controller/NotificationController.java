package com.ex.learninghub.modules.notification.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.notification.dto.response.NotificationResponse;
import com.ex.learninghub.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Thông báo", description = "Các API xem và đánh dấu đã đọc thông báo của người dùng hiện tại")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me/notifications")
    @Operation(
            summary = "Lấy danh sách thông báo của tôi",
            description = "Trả về trang thông báo của người dùng hiện tại, sắp xếp theo thời gian tạo."
    )
    public ApiResponse<Page<NotificationResponse>> getMyNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.success(
                notificationService.getMyNotifications(userPrincipal.getUser().getId(), pageable));
    }

    @GetMapping("/me/notifications/unread-count")
    @Operation(
            summary = "Đếm số thông báo chưa đọc",
            description = "Trả về số lượng thông báo chưa đọc của người dùng hiện tại."
    )
    public ApiResponse<Long> countUnread(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(notificationService.countUnread(userPrincipal.getUser().getId()));
    }

    @PatchMapping("/notifications/{id}/read")
    @Operation(
            summary = "Đánh dấu thông báo đã đọc",
            description = "Đánh dấu một thông báo cụ thể là đã đọc."
    )
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID của thông báo", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.markAsRead(id, userPrincipal.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
