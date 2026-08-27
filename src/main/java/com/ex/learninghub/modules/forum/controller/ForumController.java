package com.ex.learninghub.modules.forum.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.forum.dto.request.ForumCommentRequest;
import com.ex.learninghub.modules.forum.dto.request.ForumPostRequest;
import com.ex.learninghub.modules.forum.dto.response.ForumResponse;
import com.ex.learninghub.modules.forum.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Diễn đàn thảo luận", description = "Các API đăng bài viết và bình luận trong diễn đàn của lớp học phần")
public class ForumController {

    private final ForumService forumService;

    @PostMapping("/classes/{classId}/forum/posts")
    @Operation(
            summary = "Tạo bài viết trong diễn đàn lớp",
            description = "Người dùng thuộc lớp tạo một bài viết thảo luận mới trong diễn đàn của lớp học phần."
    )
    public ResponseEntity<ApiResponse<ForumResponse.Post>> createPost(
            @PathVariable Long classId,
            @Valid @RequestBody ForumPostRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(forumService.createPost(classId, request, principal)));
    }

    @GetMapping("/classes/{classId}/forum/posts")
    @Operation(
            summary = "Lấy danh sách bài viết của lớp",
            description = "Lấy tất cả bài viết trong diễn đàn của một lớp học phần."
    )
    public ApiResponse<List<ForumResponse.Post>> getPosts(
            @PathVariable Long classId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(forumService.getPostsByClazz(classId, principal));
    }

    @DeleteMapping("/forum/posts/{id}")
    @Operation(
            summary = "Xóa bài viết",
            description = "Xóa một bài viết (chỉ tác giả hoặc giảng viên/Admin)."
    )
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        forumService.deletePost(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forum/posts/{id}/comments")
    @Operation(
            summary = "Bình luận vào bài viết",
            description = "Thêm một bình luận mới vào một bài viết trong diễn đàn."
    )
    public ResponseEntity<ApiResponse<ForumResponse.Comment>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody ForumCommentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(forumService.addComment(id, request, principal)));
    }

    @GetMapping("/forum/posts/{id}/comments")
    @Operation(
            summary = "Lấy danh sách bình luận của bài viết",
            description = "Trả về danh sách bình luận của một bài viết trong diễn đàn."
    )
    public ApiResponse<List<ForumResponse.Comment>> getComments(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(forumService.getComments(id, principal));
    }
}
