package com.ex.learninghub.modules.quiz.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.quiz.dto.request.AiAdvisorRequest;
import com.ex.learninghub.modules.quiz.dto.response.AiAdvisorResponse;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiAdvisorServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private AiAdvisorServiceImpl aiAdvisorService;

    private User testStudent;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        testStudent = new User();
        testStudent.setId(100L);
        testStudent.setEmail("student@test.com");
        testStudent.setFullName("Nguyen Van A");
        testStudent.setRole(Role.STUDENT);

        principal = new UserPrincipal(testStudent);
    }

    @Test
    @DisplayName("analyzeCurrentStudent - throws exception when principal is null")
    void analyzeCurrentStudent_nullPrincipal_throwsException() {
        assertThatThrownBy(() -> aiAdvisorService.analyzeCurrentStudent(null))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("analyzeCurrentStudent - returns valid rule-based advisor response")
    void analyzeCurrentStudent_success() {
        Grade g1 = new Grade();
        g1.setId(1L);
        g1.setTotalScore(BigDecimal.valueOf(8.0));

        Grade g2 = new Grade();
        g2.setId(2L);
        g2.setTotalScore(BigDecimal.valueOf(9.0));

        when(userRepository.findById(100L)).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findByStudentId(100L)).thenReturn(List.of(g1, g2));

        AiAdvisorResponse response = aiAdvisorService.analyzeCurrentStudent(principal);

        assertThat(response).isNotNull();
        assertThat(response.getStudentId()).isEqualTo(100L);
        assertThat(response.getStudentName()).isEqualTo("Nguyen Van A");
        assertThat(response.getGpa()).isEqualTo(8.5);
        assertThat(response.getAcademicStatus()).isEqualTo("EXCELLENT");
        assertThat(response.getStudyPlan()).hasSize(4);
    }

    @Test
    @DisplayName("askAdvisor - handles custom query and returns response")
    void askAdvisor_withCustomQuery_success() {
        when(userRepository.findById(100L)).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findByStudentId(100L)).thenReturn(List.of());

        AiAdvisorRequest req = new AiAdvisorRequest(100L, null, "Làm sao cải thiện môn Lập trình Web?");
        AiAdvisorResponse response = aiAdvisorService.askAdvisor(req, principal);

        assertThat(response).isNotNull();
        assertThat(response.getAiAdviceSummary()).contains("Làm sao cải thiện môn Lập trình Web?");
        assertThat(response.getStudyPlan()).hasSize(4);
    }
}
