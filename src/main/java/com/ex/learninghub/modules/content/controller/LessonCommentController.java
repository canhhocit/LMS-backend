package com.ex.learninghub.modules.content.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.content.dto.request.LessonCommentRequest;
import com.ex.learninghub.modules.content.dto.response.LessonCommentResponse;
import com.ex.learninghub.modules.content.service.LessonCommentService;
import com.ex.learninghub.common.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Lesson Comments", description = "API quản lý bình luận bài giảng")
public class LessonCommentController {

    private final LessonCommentService commentService;

    @GetMapping("/{lessonId}/comments")
    @Operation(summary = "Lấy danh sách bình luận của bài giảng")
    public ApiResponse<List<LessonCommentResponse>> getComments(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(commentService.getComments(lessonId, principal));
    }

    @PostMapping("/{lessonId}/comments")
    @Operation(summary = "Thêm bình luận mới vào bài giảng")
    public ApiResponse<LessonCommentResponse> addComment(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonCommentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(commentService.addComment(lessonId, request, principal));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Xóa bình luận")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        commentService.deleteComment(commentId, principal);
        return ApiResponse.success(null);
    }
}
