package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import com.ex.learninghub.modules.curriculum.repository.CurriculumRepository;
import com.ex.learninghub.modules.grading.dto.request.GpaScaleRuleRequest;
import com.ex.learninghub.modules.grading.dto.request.GradingPolicyRequest;
import com.ex.learninghub.modules.grading.dto.response.GradingPolicyResponse;
import com.ex.learninghub.modules.grading.entity.GradingPolicy;
import com.ex.learninghub.modules.grading.repository.GpaScaleRuleRepository;
import com.ex.learninghub.modules.grading.repository.GradingPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradingPolicyServiceImplTest {

    @Mock
    private GradingPolicyRepository policyRepository;

    @Mock
    private GpaScaleRuleRepository ruleRepository;

    @Mock
    private CurriculumRepository curriculumRepository;

    @InjectMocks
    private GradingPolicyServiceImpl gradingPolicyService;

    private Curriculum curriculum;

    @BeforeEach
    void setUp() {
        curriculum = Curriculum.builder().name("K2024").build();
        curriculum.setId(10L);
    }

    @Test
    void updateGradingPolicy_succeeds_whenSumIsOne() {
        when(curriculumRepository.findById(10L)).thenReturn(Optional.of(curriculum));
        when(policyRepository.findByCurriculumId(10L)).thenReturn(Optional.empty());
        when(policyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GradingPolicyRequest req = GradingPolicyRequest.builder()
                .attendanceWeight(new BigDecimal("0.100"))
                .midtermWeight(new BigDecimal("0.300"))
                .finalWeight(new BigDecimal("0.600"))
                .build();

        GradingPolicyResponse resp = gradingPolicyService.updateGradingPolicy(10L, req);

        assertThat(resp.getAttendanceWeight()).isEqualTo(new BigDecimal("0.100"));
        assertThat(resp.getMidtermWeight()).isEqualTo(new BigDecimal("0.300"));
        assertThat(resp.getFinalWeight()).isEqualTo(new BigDecimal("0.600"));
    }

    @Test
    void updateGradingPolicy_throwsError_whenSumNotOne() {
        when(curriculumRepository.findById(10L)).thenReturn(Optional.of(curriculum));

        GradingPolicyRequest req = GradingPolicyRequest.builder()
                .attendanceWeight(new BigDecimal("0.200"))
                .midtermWeight(new BigDecimal("0.300"))
                .finalWeight(new BigDecimal("0.600"))
                .build();

        assertThatThrownBy(() -> gradingPolicyService.updateGradingPolicy(10L, req))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.GRADING_POLICY_WEIGHTS_INVALID);
    }

    @Test
    void updateGpaScaleRules_throwsError_whenMinScoreNotDecreasing() {
        when(curriculumRepository.findById(10L)).thenReturn(Optional.of(curriculum));

        List<GpaScaleRuleRequest> invalidRules = List.of(
                GpaScaleRuleRequest.builder().minScore10(new BigDecimal("8.00")).gpa4(new BigDecimal("3.50")).sortOrder(1).build(),
                GpaScaleRuleRequest.builder().minScore10(new BigDecimal("9.00")).gpa4(new BigDecimal("4.00")).sortOrder(2).build()
        );

        assertThatThrownBy(() -> gradingPolicyService.updateGpaScaleRules(10L, invalidRules))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.GPA_SCALE_INVALID);
    }
}
