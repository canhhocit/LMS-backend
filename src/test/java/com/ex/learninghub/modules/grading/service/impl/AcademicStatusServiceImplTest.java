package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.grading.dto.response.AcademicStatusResponse;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.notification.service.NotificationService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicStatusServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AcademicStatusServiceImpl academicStatusService;

    private User student;
    private Course course1, course2, course3;

    @BeforeEach
    void setUp() {
        student = User.builder().email("sv@test").role(Role.STUDENT).build();
        student.setId(1L);

        course1 = Course.builder().code("INT101").title("Java").credit(3).build();
        course1.setId(10L);
        course2 = Course.builder().code("INT102").title("DB").credit(3).build();
        course2.setId(11L);
        course3 = Course.builder().code("INT103").title("Web").credit(2).build();
        course3.setId(12L);

        // @Value không được inject bởi @InjectMocks; thiết lập thủ công.
        ReflectionTestUtils.setField(academicStatusService, "passScore", new BigDecimal("5.0"));
        ReflectionTestUtils.setField(academicStatusService, "warningGpa", new BigDecimal("2.0"));
        ReflectionTestUtils.setField(academicStatusService, "probationGpa", new BigDecimal("1.5"));
    }

    private Grade grade(Long courseId, BigDecimal score) {
        Course c = course1.getId().equals(courseId) ? course1
                : course2.getId().equals(courseId) ? course2 : course3;
        return Grade.builder()
                .clazz(com.ex.learninghub.modules.course.entity.Clazz.builder().course(c).build())
                .student(student)
                .totalScore(score)
                .build();
    }

    @Test
    void getMyAcademicStatus_noGrades_returnsZeroGpa() {
        when(gradeRepository.findByStudentId(1L)).thenReturn(List.of());

        AcademicStatusResponse resp = academicStatusService.getMyAcademicStatus(principal(1L));

        assertThat(resp.getCumulativeGpa()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resp.getTotalCourses()).isEqualTo(0);
        assertThat(resp.getPassedCourses()).isEqualTo(0);
        assertThat(resp.getAcademicWarning()).isFalse();
    }

    @Test
    void getMyAcademicStatus_allPassed_calculatesGpaCorrectly() {
        // 2 môn: 9.0 (4.0*3) + 8.0 (3.5*3) = 12 + 10.5 = 22.5 / 6 = 3.75
        when(gradeRepository.findByStudentId(1L)).thenReturn(List.of(
                grade(10L, new BigDecimal("9.0")),
                grade(11L, new BigDecimal("8.0"))
        ));

        AcademicStatusResponse resp = academicStatusService.getMyAcademicStatus(principal(1L));

        assertThat(resp.getTotalCourses()).isEqualTo(2);
        assertThat(resp.getPassedCourses()).isEqualTo(2);
        assertThat(resp.getTotalCredits()).isEqualTo(6);
        assertThat(resp.getPassedCredits()).isEqualTo(6);
        assertThat(resp.getCumulativeGpa()).isEqualByComparingTo(new BigDecimal("3.75"));
        assertThat(resp.getAcademicWarning()).isFalse();
    }

    @Test
    void getMyAcademicStatus_withFailedCourse_includesZeroInGpa() {
        // 9.0 (4.0*3) + 4.0 (1.0*3 - theo bảng quy đổi scoreToGpa4, 4.0 -> 1.0) = 15 / 6 = 2.50
        when(gradeRepository.findByStudentId(1L)).thenReturn(List.of(
                grade(10L, new BigDecimal("9.0")),
                grade(11L, new BigDecimal("4.0")) // trượt (dưới passScore=5.0) nhưng vẫn có điểm
        ));

        AcademicStatusResponse resp = academicStatusService.getMyAcademicStatus(principal(1L));

        assertThat(resp.getTotalCourses()).isEqualTo(2);
        assertThat(resp.getPassedCourses()).isEqualTo(1);
        assertThat(resp.getFailedCourses()).hasSize(1);
        assertThat(resp.getFailedCourses().get(0).getCourseCode()).isEqualTo("INT102");
        // GPA = (4.0*3 + 1.0*3) / 6 = 2.50
        assertThat(resp.getCumulativeGpa()).isEqualByComparingTo(new BigDecimal("2.50"));
        // 2.50 >= warningGpa(2.0) nhưng < probationGpa(1.5) là false → không warning
        assertThat(resp.getAcademicWarning()).isFalse();
    }

    @Test
    void getMyAcademicStatus_probation_whenGpaBelow1_5() {
        // 3.0 (0.0*3) + 3.0 (0.0*3) = 0 / 6 = 0.0  -> warningLevel 3 (probation)
        when(gradeRepository.findByStudentId(1L)).thenReturn(List.of(
                grade(10L, new BigDecimal("3.0")),
                grade(11L, new BigDecimal("3.0"))
        ));

        AcademicStatusResponse resp = academicStatusService.getMyAcademicStatus(principal(1L));

        assertThat(resp.getCumulativeGpa()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resp.getAcademicWarning()).isTrue();
        assertThat(resp.getWarningLevel()).isEqualTo(3); // probation
    }

    @Test
    void scanAndWarnAcademicProbation_callsNotificationForLowGpa() {
        User s1 = User.builder().email("a@test").role(Role.STUDENT).build(); s1.setId(1L);
        User s2 = User.builder().email("b@test").role(Role.STUDENT).build(); s2.setId(2L);
        User s3 = User.builder().email("c@test").role(Role.STUDENT).build(); s3.setId(3L);

        when(userRepository.findByRole(Role.STUDENT)).thenReturn(List.of(s1, s2, s3));

        // s1: GPA 1.0 (probation) -> warn
        // s2: GPA 3.0 -> no warn
        // s3: GPA 0.0 (probation) -> warn
        when(gradeRepository.findByStudentId(1L)).thenReturn(List.of(grade(10L, new BigDecimal("4.0"))));
        when(gradeRepository.findByStudentId(2L)).thenReturn(List.of(grade(10L, new BigDecimal("9.0"))));
        when(gradeRepository.findByStudentId(3L)).thenReturn(List.of(grade(10L, new BigDecimal("3.0"))));

        int warned = academicStatusService.scanAndWarnAcademicProbation();

        assertThat(warned).isEqualTo(2); // s1 và s3
        verify(notificationService, times(2)).notifyUser(
                anyLong(), eq(NotificationType.ACADEMIC_WARNING), anyString(), anyString(), isNull());
    }

    private UserPrincipal principal(long id) {
        User u = User.builder().build();
        u.setId(id);
        return new UserPrincipal(u);
    }
}
