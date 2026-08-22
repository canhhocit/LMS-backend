package com.ex.learninghub.modules.forum.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.forum.dto.request.ForumCommentRequest;
import com.ex.learninghub.modules.forum.dto.request.ForumPostRequest;
import com.ex.learninghub.modules.forum.dto.response.ForumResponse;
import com.ex.learninghub.modules.forum.service.ForumService;
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
public class ForumController {

    private final ForumService forumService;

    @PostMapping("/classes/{classId}/forum/posts")
    public ResponseEntity<ApiResponse<ForumResponse.Post>> createPost(
            @PathVariable Long classId,
            @Valid @RequestBody ForumPostRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(forumService.createPost(classId, request, principal)));
    }

    @GetMapping("/classes/{classId}/forum/posts")
    public ApiResponse<List<ForumResponse.Post>> getPosts(
            @PathVariable Long classId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(forumService.getPostsByClazz(classId, principal));
    }

    @DeleteMapping("/forum/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        forumService.deletePost(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forum/posts/{id}/comments")
    public ResponseEntity<ApiResponse<ForumResponse.Comment>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody ForumCommentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(forumService.addComment(id, request, principal)));
    }

    @GetMapping("/forum/posts/{id}/comments")
    public ApiResponse<List<ForumResponse.Comment>> getComments(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(forumService.getComments(id, principal));
    }
}
