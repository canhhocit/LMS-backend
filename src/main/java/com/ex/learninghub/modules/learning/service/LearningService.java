package com.ex.learninghub.modules.learning.service;

import com.ex.learninghub.modules.learning.dto.response.EnrollmentResponse;
import com.ex.learninghub.modules.learning.dto.response.ProgressResponse;
import java.util.List;

public interface LearningService {
    EnrollmentResponse enrollCourse(Long courseId, String email);
    List<EnrollmentResponse> getMyEnrollments(String email);
    
    ProgressResponse updateProgress(Long enrollmentId, Long lessonId, boolean isCompleted);
    List<ProgressResponse> getProgress(Long enrollmentId);
}
