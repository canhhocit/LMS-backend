package com.ex.learninghub.modules.forum.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.forum.dto.request.ForumCommentRequest;
import com.ex.learninghub.modules.forum.dto.request.ForumPostRequest;
import com.ex.learninghub.modules.forum.dto.response.ForumResponse;

import java.util.List;

public interface ForumService {

    ForumResponse.Post createPost(Long clazzId, ForumPostRequest request, UserPrincipal principal);

    List<ForumResponse.Post> getPostsByClazz(Long clazzId, UserPrincipal principal);

    void deletePost(Long postId, UserPrincipal principal);

    ForumResponse.Comment addComment(Long postId, ForumCommentRequest request, UserPrincipal principal);

    List<ForumResponse.Comment> getComments(Long postId, UserPrincipal principal);
}
