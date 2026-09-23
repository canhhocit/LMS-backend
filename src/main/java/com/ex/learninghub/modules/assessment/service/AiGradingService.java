package com.ex.learninghub.modules.assessment.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.dto.response.AiGradingResponse;
import com.ex.learninghub.modules.assessment.dto.response.SubmissionResponse;

public interface AiGradingService {
    AiGradingResponse evaluateSubmission(Long submissionId, UserPrincipal principal);
    SubmissionResponse applyAiGrade(Long submissionId, UserPrincipal principal);
}
