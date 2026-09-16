package com.ex.learninghub.modules.quiz.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.quiz.dto.request.AiAdvisorRequest;
import com.ex.learninghub.modules.quiz.dto.response.AiAdvisorResponse;

public interface AiAdvisorService {

    /**
     * Phân tích và sinh đề xuất cố vấn học tập cho sinh viên hiện tại.
     */
    AiAdvisorResponse analyzeCurrentStudent(UserPrincipal principal);

    /**
     * Phân tích sinh viên chỉ định (dành cho Giảng viên / Admin).
     */
    AiAdvisorResponse analyzeStudentById(Long studentId, UserPrincipal principal);

    /**
     * Tham vấn trực tiếp câu hỏi cụ thể với Cố vấn AI.
     */
    AiAdvisorResponse askAdvisor(AiAdvisorRequest request, UserPrincipal principal);
}
