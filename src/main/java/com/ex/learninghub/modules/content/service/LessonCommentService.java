package com.ex.learninghub.modules.content.service;

import com.ex.learninghub.modules.content.dto.request.LessonCommentRequest;
import com.ex.learninghub.modules.content.dto.response.LessonCommentResponse;
import com.ex.learninghub.common.security.UserPrincipal;

import java.util.List;

public interface LessonCommentService {
    List<LessonCommentResponse> getComments(Long lessonId, UserPrincipal principal);
    LessonCommentResponse addComment(Long lessonId, LessonCommentRequest request, UserPrincipal principal);
    void deleteComment(Long commentId, UserPrincipal principal);
}
