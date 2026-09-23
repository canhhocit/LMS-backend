package com.ex.learninghub.modules.assessment.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.dto.response.AiGradingResponse;
import com.ex.learninghub.modules.assessment.dto.response.SubmissionResponse;
import com.ex.learninghub.modules.assessment.entity.Assignment;
import com.ex.learninghub.modules.assessment.entity.Submission;
import com.ex.learninghub.modules.assessment.repository.SubmissionRepository;
import com.ex.learninghub.modules.assessment.service.AssessmentService;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiGradingServiceImplTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AiClientService aiClientService;

    @InjectMocks
    private AiGradingServiceImpl aiGradingService;

    private User lecturer;
    private User student;
    private Assignment assignment;
    private Submission submission;
    private UserPrincipal lecturerPrincipal;

    @BeforeEach
    void setUp() {
        lenient().when(aiClientService.isAiConfigured()).thenReturn(false);

        lecturer = User.builder().email("gv@test.com").role(Role.LECTURER).build();
        lecturer.setId(10L);

        student = User.builder().email("sv@test.com").fullName("Nguyen Van B").role(Role.STUDENT).build();
        student.setId(20L);

        Clazz clazz = Clazz.builder().className("INT101").lecturer(lecturer).build();
        clazz.setId(100L);

        assignment = Assignment.builder()
                .title("Bài tập 1: Lập trình Java")
                .description("Viết chương trình Quản lý Sinh viên")
                .maxScore(BigDecimal.valueOf(10))
                .clazz(clazz)
                .build();
        assignment.setId(50L);

        submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .fileUrl("http://example.com/file.zip")
                .isLate(false)
                .build();
        submission.setId(500L);

        lecturerPrincipal = new UserPrincipal(lecturer);
    }

    @Test
    void evaluateSubmission_ruleBasedFallback_returnsValidScore() {
        when(submissionRepository.findById(500L)).thenReturn(Optional.of(submission));

        AiGradingResponse response = aiGradingService.evaluateSubmission(500L, lecturerPrincipal);

        assertThat(response).isNotNull();
        assertThat(response.getSubmissionId()).isEqualTo(500L);
        assertThat(response.getSuggestedScore()).isEqualTo(BigDecimal.valueOf(8.00).setScale(2));
        assertThat(response.getStrengths()).isNotEmpty();
    }

    @Test
    void evaluateSubmission_throwsForbidden_whenOtherStudentEvaluates() {
        User otherStudent = User.builder().email("other@test.com").role(Role.STUDENT).build();
        otherStudent.setId(99L);
        UserPrincipal otherPrincipal = new UserPrincipal(otherStudent);

        when(submissionRepository.findById(500L)).thenReturn(Optional.of(submission));

        assertThatThrownBy(() -> aiGradingService.evaluateSubmission(500L, otherPrincipal))
                .isInstanceOf(AppException.class);
    }

    @Test
    void applyAiGrade_callsAssessmentServiceGradeSubmission() {
        when(submissionRepository.findById(500L)).thenReturn(Optional.of(submission));

        SubmissionResponse mockSubResp = SubmissionResponse.builder()
                .id(500L)
                .score(BigDecimal.valueOf(8.0))
                .feedback("Feedback")
                .build();

        when(assessmentService.gradeSubmission(eq(500L), any(), eq(lecturerPrincipal))).thenReturn(mockSubResp);

        SubmissionResponse result = aiGradingService.applyAiGrade(500L, lecturerPrincipal);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(500L);
    }
}
