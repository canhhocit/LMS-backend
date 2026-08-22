package com.ex.learninghub.modules.enrollment.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.enrollment.dto.response.ProgressResponse;

public interface ProgressService {

    void markLessonCompleted(Long enrollmentId, Long lessonId, UserPrincipal principal);

    ProgressResponse getProgressByEnrollment(Long enrollmentId, UserPrincipal principal);
}
