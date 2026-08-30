package com.ex.learninghub.modules.grading.service;

import com.ex.learninghub.modules.grading.dto.request.GpaScaleRuleRequest;
import com.ex.learninghub.modules.grading.dto.request.GradingPolicyRequest;
import com.ex.learninghub.modules.grading.dto.response.GpaScaleRuleResponse;
import com.ex.learninghub.modules.grading.dto.response.GradingPolicyResponse;

import java.util.List;

public interface GradingPolicyService {
    GradingPolicyResponse getGradingPolicy(Long curriculumId);
    GradingPolicyResponse updateGradingPolicy(Long curriculumId, GradingPolicyRequest request);
    List<GpaScaleRuleResponse> getGpaScaleRules(Long curriculumId);
    List<GpaScaleRuleResponse> updateGpaScaleRules(Long curriculumId, List<GpaScaleRuleRequest> requests);
}
