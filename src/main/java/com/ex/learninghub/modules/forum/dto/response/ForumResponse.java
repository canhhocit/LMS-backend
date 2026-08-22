package com.ex.learninghub.modules.forum.dto.response;

import com.ex.learninghub.modules.forum.entity.ForumComment;
import com.ex.learninghub.modules.forum.entity.ForumPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ForumResponse {

    @Getter
    @Builder
    public static class Post {
        private Long id;
        private Long clazzId;
        private Long authorId;
        private String authorName;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private List<Comment> comments;

        public static Post from(ForumPost post) {
            return Post.builder()
                    .id(post.getId())
                    .clazzId(post.getClazz().getId())
                    .authorId(post.getAuthor().getId())
                    .authorName(post.getAuthor().getFullName())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Comment {
        private Long id;
        private Long postId;
        private Long authorId;
        private String authorName;
        private String content;
        private LocalDateTime createdAt;

        public static Comment from(ForumComment comment) {
            return Comment.builder()
                    .id(comment.getId())
                    .postId(comment.getPost().getId())
                    .authorId(comment.getAuthor().getId())
                    .authorName(comment.getAuthor().getFullName())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }
}
