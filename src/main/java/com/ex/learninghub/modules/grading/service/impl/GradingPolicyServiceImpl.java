package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import com.ex.learninghub.modules.curriculum.repository.CurriculumRepository;
import com.ex.learninghub.modules.grading.dto.request.GpaScaleRuleRequest;
import com.ex.learninghub.modules.grading.dto.request.GradingPolicyRequest;
import com.ex.learninghub.modules.grading.dto.response.GpaScaleRuleResponse;
import com.ex.learninghub.modules.grading.dto.response.GradingPolicyResponse;
import com.ex.learninghub.modules.grading.entity.GpaScaleRule;
import com.ex.learninghub.modules.grading.entity.GradingPolicy;
import com.ex.learninghub.modules.grading.repository.GpaScaleRuleRepository;
import com.ex.learninghub.modules.grading.repository.GradingPolicyRepository;
import com.ex.learninghub.modules.grading.service.GradingPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradingPolicyServiceImpl implements GradingPolicyService {

    private final GradingPolicyRepository policyRepository;
    private final GpaScaleRuleRepository ruleRepository;
    private final CurriculumRepository curriculumRepository;

    @Override
    @Transactional(readOnly = true)
    public GradingPolicyResponse getGradingPolicy(Long curriculumId) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        return policyRepository.findByCurriculumId(curriculumId)
                .map(GradingPolicyResponse::from)
                .orElseGet(() -> GradingPolicyResponse.builder()
                        .curriculumId(curriculum.getId())
                        .attendanceWeight(new BigDecimal("0.000"))
                        .midtermWeight(new BigDecimal("0.400"))
                        .finalWeight(new BigDecimal("0.600"))
                        .build());
    }

    @Override
    @Transactional
    public GradingPolicyResponse updateGradingPolicy(Long curriculumId, GradingPolicyRequest request) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        BigDecimal sum = request.getAttendanceWeight()
                .add(request.getMidtermWeight())
                .add(request.getFinalWeight());

        // Validate sum is between 0.999 and 1.001
        if (sum.subtract(BigDecimal.ONE).abs().doubleValue() > 0.001) {
            throw new AppException(ErrorCode.GRADING_POLICY_WEIGHTS_INVALID);
        }

        GradingPolicy policy = policyRepository.findByCurriculumId(curriculumId)
                .orElseGet(() -> GradingPolicy.builder().curriculum(curriculum).build());

        policy.setAttendanceWeight(request.getAttendanceWeight());
        policy.setMidtermWeight(request.getMidtermWeight());
        policy.setFinalWeight(request.getFinalWeight());

        return GradingPolicyResponse.from(policyRepository.save(policy));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GpaScaleRuleResponse> getGpaScaleRules(Long curriculumId) {
        curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        return ruleRepository.findByCurriculumIdOrderBySortOrderAsc(curriculumId).stream()
                .map(GpaScaleRuleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GpaScaleRuleResponse> updateGpaScaleRules(Long curriculumId, List<GpaScaleRuleRequest> requests) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (requests == null || requests.isEmpty()) {
            throw new AppException(ErrorCode.GPA_SCALE_INVALID);
        }

        // Sort by sortOrder
        List<GpaScaleRuleRequest> sorted = requests.stream()
                .sorted(Comparator.comparingInt(GpaScaleRuleRequest::getSortOrder))
                .toList();

        // Validate minScore10 is strictly decreasing as sortOrder increases, and gpa4 between 0 and 4
        BigDecimal prevMinScore = new BigDecimal("10.01");
        for (GpaScaleRuleRequest req : sorted) {
            if (req.getGpa4().compareTo(BigDecimal.ZERO) < 0 || req.getGpa4().compareTo(new BigDecimal("4.00")) > 0) {
                throw new AppException(ErrorCode.GPA_SCALE_INVALID);
            }
            if (req.getMinScore10().compareTo(BigDecimal.ZERO) < 0 || req.getMinScore10().compareTo(new BigDecimal("10.00")) > 0) {
                throw new AppException(ErrorCode.GPA_SCALE_INVALID);
            }
            if (req.getMinScore10().compareTo(prevMinScore) >= 0) {
                throw new AppException(ErrorCode.GPA_SCALE_INVALID);
            }
            prevMinScore = req.getMinScore10();
        }

        // Delete old rules and save new
        ruleRepository.deleteByCurriculumId(curriculumId);

        List<GpaScaleRule> newRules = new ArrayList<>();
        for (GpaScaleRuleRequest req : sorted) {
            GpaScaleRule rule = GpaScaleRule.builder()
                    .curriculum(curriculum)
                    .minScore10(req.getMinScore10())
                    .gpa4(req.getGpa4())
                    .sortOrder(req.getSortOrder())
                    .build();
            newRules.add(ruleRepository.save(rule));
        }

        return newRules.stream().map(GpaScaleRuleResponse::from).collect(Collectors.toList());
    }
}
