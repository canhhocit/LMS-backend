package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.grading.dto.response.QrSessionResponse;
import com.ex.learninghub.modules.grading.repository.AttendanceRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrCodeAttendanceServiceImplTest {

    @Mock
    private ClazzRepository clazzRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private QrCodeAttendanceServiceImpl qrCodeAttendanceService;
    private User lecturer;
    private Clazz clazz;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        qrCodeAttendanceService = new QrCodeAttendanceServiceImpl(
                clazzRepository, attendanceRepository, userRepository, redisTemplate
        );

        lecturer = User.builder().email("lecturer@test.com").role(Role.LECTURER).build();
        lecturer.setId(10L);

        clazz = Clazz.builder().className("JAVA_CLASS_01").classCode("JAVA01").build();
        clazz.setId(100L);
    }

    @Test
    void generateQrSession_returnsValidQrSession() {
        when(clazzRepository.existsById(100L)).thenReturn(true);

        QrSessionResponse response = qrCodeAttendanceService.generateQrSession(100L, new UserPrincipal(lecturer));

        assertThat(response).isNotNull();
        assertThat(response.getClassId()).isEqualTo(100L);
        assertThat(response.getOtpCode()).isNotNull().hasSize(6);
        assertThat(response.getExpiresInSeconds()).isEqualTo(10);
    }
}
