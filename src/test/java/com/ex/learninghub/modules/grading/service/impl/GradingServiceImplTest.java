package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.enums.AttendanceStatus;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.grading.dto.request.GradeRequest;
import com.ex.learninghub.modules.grading.dto.response.GradeResponse;
import com.ex.learninghub.modules.grading.repository.AttendanceRepository;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradingServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private ClazzRepository clazzRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.ex.learninghub.modules.notification.service.NotificationService notificationService;

    @InjectMocks
    private GradingServiceImpl gradingService;

    private User lecturer;
    private User student;
    private Clazz clazz;
    private UserPrincipal lecturerPrincipal;

    @BeforeEach
    void setUp() {
        lecturer = User.builder().email("gv@test.edu.vn").role(Role.LECTURER).build();
        lecturer.setId(100L);
        student = User.builder().email("sv@test.edu.vn").role(Role.STUDENT).build();
        student.setId(1L);

        Course course = Course.builder().title("Java").credit(3).build();
        course.setId(5L);

        clazz = Clazz.builder().className("INT1001").lecturer(lecturer).course(course).build();
        clazz.setId(10L);

        lecturerPrincipal = new UserPrincipal(lecturer);
    }

    private GradeRequest gradeRequest(String midterm, String fin) {
        GradeRequest request = new GradeRequest();
        request.setStudentId(1L);
        request.setMidtermScore(new BigDecimal(midterm));
        request.setFinalScore(new BigDecimal(fin));
        return request;
    }

    @Test
    void upsertGrade_calculatesTotalScore_correctly() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(attendanceRepository.countByClazzIdAndStudentId(anyLong(), anyLong())).thenReturn(0L);
        when(gradeRepository.findByClazzIdAndStudentId(10L, 1L))
                .thenReturn(Optional.of(com.ex.learninghub.modules.grading.entity.Grade.builder()
                        .clazz(clazz).student(student).build()));
        when(gradeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GradeResponse response = gradingService.upsertGrade(10L, gradeRequest("8", "9"), lecturerPrincipal);

        // 8 * 0.4 + 9 * 0.6 = 3.2 + 5.4 = 8.6
        assertThat(response.getTotalScore()).isEqualByComparingTo(new BigDecimal("8.60"));
    }

    @Test
    void upsertGrade_absentRatioExceeded_throwsAttendanceNotQualified() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(attendanceRepository.countByClazzIdAndStudentId(anyLong(), anyLong())).thenReturn(10L);
        when(attendanceRepository.countByClazzIdAndStudentIdAndStatus(anyLong(), anyLong(),
                any(AttendanceStatus.class))).thenReturn(5L); // 50% absent > 20% max

        assertThatThrownBy(() -> gradingService.upsertGrade(10L, gradeRequest("8", "9"), lecturerPrincipal))
                .isInstanceOf(AppException.class);
    }

    @Test
    void upsertGrade_lecturerNotOwner_throwsForbidden() {
        User otherLecturer = User.builder().email("other@test.edu.vn").role(Role.LECTURER).build();
        otherLecturer.setId(200L);

        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));

        assertThatThrownBy(() -> gradingService.upsertGrade(10L, gradeRequest("8", "9"),
                new UserPrincipal(otherLecturer)))
                .isInstanceOf(AppException.class);
    }
}