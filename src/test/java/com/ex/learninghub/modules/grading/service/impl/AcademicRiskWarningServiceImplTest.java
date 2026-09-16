package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.config.AppProperties;
import com.ex.learninghub.common.enums.AttendanceStatus;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.grading.dto.response.AcademicRiskResponse;
import com.ex.learninghub.modules.grading.repository.AttendanceRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicRiskWarningServiceImplTest {

    @Mock
    private ClazzRepository clazzRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private AcademicRiskWarningServiceImpl academicRiskWarningService;

    private Clazz clazz;
    private User student;
    private AppProperties.Attendance attendanceConfig;

    @BeforeEach
    void setUp() {
        clazz = Clazz.builder().className("DATABASES_01").classCode("DB01").build();
        clazz.setId(1L);

        student = User.builder().email("student@test.com").fullName("Nguyen Van A").build();
        student.setId(10L);

        attendanceConfig = new AppProperties.Attendance();
        attendanceConfig.setMaxAbsentRatio(0.2);
    }

    @Test
    void calculateStudentRisk_returnsSafe_whenAbsentRatioLow() {
        when(clazzRepository.findById(1L)).thenReturn(Optional.of(clazz));
        when(userRepository.findById(10L)).thenReturn(Optional.of(student));
        when(appProperties.getAttendance()).thenReturn(attendanceConfig);
        when(attendanceRepository.countByClazzIdAndStudentId(1L, 10L)).thenReturn(10L);
        when(attendanceRepository.countByClazzIdAndStudentIdAndStatus(1L, 10L, AttendanceStatus.ABSENT)).thenReturn(1L);

        AcademicRiskResponse response = academicRiskWarningService.calculateStudentRisk(1L, 10L);

        assertThat(response).isNotNull();
        assertThat(response.getAbsentRatio()).isEqualTo(0.1);
        assertThat(response.getRiskLevel()).isEqualTo("SAFE");
    }

    @Test
    void calculateStudentRisk_returnsCritical_whenAbsentRatioExceedsMax() {
        when(clazzRepository.findById(1L)).thenReturn(Optional.of(clazz));
        when(userRepository.findById(10L)).thenReturn(Optional.of(student));
        when(appProperties.getAttendance()).thenReturn(attendanceConfig);
        when(attendanceRepository.countByClazzIdAndStudentId(1L, 10L)).thenReturn(10L);
        when(attendanceRepository.countByClazzIdAndStudentIdAndStatus(1L, 10L, AttendanceStatus.ABSENT)).thenReturn(3L);

        AcademicRiskResponse response = academicRiskWarningService.calculateStudentRisk(1L, 10L);

        assertThat(response).isNotNull();
        assertThat(response.getAbsentRatio()).isEqualTo(0.3);
        assertThat(response.getRiskLevel()).isEqualTo("CRITICAL");
    }
}
