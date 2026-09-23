package com.ex.learninghub.common.ai.service.impl;

import com.ex.learninghub.common.ai.entity.UserAiPreference;
import com.ex.learninghub.common.ai.repository.AiPromptTemplateRepository;
import com.ex.learninghub.common.ai.repository.UserAiPreferenceRepository;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.user.entity.User;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonalizationEngineImplTest {

    @Mock
    private AiPromptTemplateRepository templateRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private UserAiPreferenceRepository userAiPreferenceRepository;

    @InjectMocks
    private PersonalizationEngineImpl personalizationEngine;

    private User weakStudent;
    private User excellentStudent;

    @BeforeEach
    void setUp() {
        weakStudent = User.builder().fullName("Nguyen Van Weak").email("weak@test.com").build();
        weakStudent.setId(101L);

        excellentStudent = User.builder().fullName("Tran Van Excellent").email("excellent@test.com").build();
        excellentStudent.setId(102L);
    }

    @Test
    void buildPersonalizedSystemPrompt_adaptsForWeakStudent() {
        Grade g1 = new Grade();
        g1.setTotalScore(BigDecimal.valueOf(5.0));
        when(gradeRepository.findByStudentId(101L)).thenReturn(List.of(g1));
        when(templateRepository.findByIsDefaultTrue()).thenReturn(Optional.empty());

        String systemPrompt = personalizationEngine.buildPersonalizedSystemPrompt(weakStudent, null, null);

        assertThat(systemPrompt).contains("Nguyen Van Weak");
        assertThat(systemPrompt).contains("kiên nhẫn");
        assertThat(systemPrompt).contains("5.00");
    }

    @Test
    void buildPersonalizedSystemPrompt_adaptsForExcellentStudent() {
        Grade g1 = new Grade();
        g1.setTotalScore(BigDecimal.valueOf(9.5));
        when(gradeRepository.findByStudentId(102L)).thenReturn(List.of(g1));
        when(templateRepository.findByIsDefaultTrue()).thenReturn(Optional.empty());

        String systemPrompt = personalizationEngine.buildPersonalizedSystemPrompt(excellentStudent, null, "Thân thiện nhưng sâu sắc");

        assertThat(systemPrompt).contains("Tran Van Excellent");
        assertThat(systemPrompt).contains("Xuất sắc");
        assertThat(systemPrompt).contains("Thân thiện nhưng sâu sắc");
    }

    @Test
    void buildPersonalizedSystemPrompt_includesUserAiPreferences() {
        Grade g1 = new Grade();
        g1.setTotalScore(BigDecimal.valueOf(8.0));
        when(gradeRepository.findByStudentId(101L)).thenReturn(List.of(g1));
        when(templateRepository.findByIsDefaultTrue()).thenReturn(Optional.empty());

        UserAiPreference pref = UserAiPreference.builder()
                .preferredCallName("Xưng em - gọi Thầy")
                .aiTone("CONCISE")
                .customToneDescription("Súc tích ngắn gọn")
                .responseLength("STEP_BY_STEP")
                .customContext("Chuyên ngành CNTT")
                .build();
        when(userAiPreferenceRepository.findByUserId(101L)).thenReturn(Optional.of(pref));

        String systemPrompt = personalizationEngine.buildPersonalizedSystemPrompt(weakStudent, null, null);

        assertThat(systemPrompt).contains("Xưng em - gọi Thầy");
        assertThat(systemPrompt).contains("CONCISE");
        assertThat(systemPrompt).contains("Súc tích ngắn gọn");
        assertThat(systemPrompt).contains("STEP_BY_STEP");
        assertThat(systemPrompt).contains("Chuyên ngành CNTT");
    }
}
