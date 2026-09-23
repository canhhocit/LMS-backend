package com.ex.learninghub.modules.content.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.response.AiLessonSummaryResponse;

public interface AiLessonSummarizerService {
    AiLessonSummaryResponse summarizeLesson(Long lessonId, UserPrincipal principal);
}
