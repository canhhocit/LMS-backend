package com.ex.learninghub.modules.registration.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.ClassSchedule;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.ClassScheduleRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import com.ex.learninghub.modules.registration.repository.RegistrationPeriodRepository;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private RegistrationPeriodRepository periodRepository;

    @Mock
    private ClazzRepository clazzRepository;

    @Mock
    private ClassScheduleRepository scheduleRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private com.ex.learninghub.modules.curriculum.repository.CoursePrerequisiteRepository prerequisiteRepository;
    @Mock
    private com.ex.learninghub.modules.grading.repository.GradeRepository gradeRepository;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private User student;
    private RegistrationPeriod openPeriod;
    private RegistrationPeriod closedPeriod;
    private Clazz newClazz;

    @BeforeEach
    void setUp() {
        student = User.builder().email("sv@test").role(Role.STUDENT).build();
        student.setId(1L);

        Course c1 = Course.builder().title("Java").credit(3).code("INT101").build();
        c1.setId(11L);

        Course c2 = Course.builder().title("DB").credit(2).code("INT102").build();
        c2.setId(12L);

        newClazz = Clazz.builder().className("INT101-01").course(c1).build();
        newClazz.setId(20L);

        LocalDateTime now = LocalDateTime.now();
        openPeriod = RegistrationPeriod.builder()
                .name("HK1 2025-2026").semester("1").academicYear("2025-2026")
                .openAt(now.minusDays(1)).closeAt(now.plusDays(7))
                .maxCredits(20).isActive(true).build();
        openPeriod.setId(99L);

        closedPeriod = RegistrationPeriod.builder()
                .name("HK2").semester("2").academicYear("2025-2026")
                .openAt(now.plusDays(10)).closeAt(now.plusDays(20))
                .isActive(true).build();
    }

    private ClassSchedule sched(Long clazzId, Integer day, int start, int end) {
        ClassSchedule s = ClassSchedule.builder()
                .clazzId(clazzId).dayOfWeek(day)
                .startPeriod(start).endPeriod(end).build();
        s.setId(clazzId * 10 + end);
        return s;
    }

    private Enrollment existingEnrollment(Long id, Clazz clazz) {
        Enrollment e = Enrollment.builder()
                .student(student).clazz(clazz).build();
        e.setId(id);
        return e;
    }

    @Test
    void register_throwsClosed_whenNoActivePeriod() {
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.register(20L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void register_throwsClosed_whenPeriodNotInWindow() {
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.of(closedPeriod));

        assertThatThrownBy(() -> registrationService.register(20L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void register_throwsExists_whenAlreadyEnrolled() {
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.of(openPeriod));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 20L)).thenReturn(true);

        assertThatThrownBy(() -> registrationService.register(20L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void register_throwsClazzNotFound() {
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.of(openPeriod));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 20L)).thenReturn(false);
        when(clazzRepository.findById(20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.register(20L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void register_succeeds_whenWithinLimits() {
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.of(openPeriod));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 20L)).thenReturn(false);
        when(clazzRepository.findById(20L)).thenReturn(Optional.of(newClazz));
        when(scheduleRepository.findByClazzId(20L)).thenReturn(List.of(sched(20L, 2, 1, 3)));
        when(enrollmentRepository.findByStudentId(1L)).thenReturn(new ArrayList<>());
        when(prerequisiteRepository.findByCourseId(11L)).thenReturn(List.of());
        when(enrollmentRepository.save(any())).thenAnswer(inv -> {
            Enrollment e = inv.getArgument(0);
            e.setId(500L);
            return e;
        });

        var response = registrationService.register(20L, new UserPrincipal(student));

        assertThat(response.getClazzId()).isEqualTo(20L);
        assertThat(response.getCredits()).isEqualTo(3);
        verify(enrollmentRepository, times(1)).save(any());
    }

    @Test
    void register_throwsCreditExceeded() {
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.of(openPeriod));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 20L)).thenReturn(false);
        when(clazzRepository.findById(20L)).thenReturn(Optional.of(newClazz));

        // student already has 18 credits; max=20; new adds 3 → over limit
        Clazz existingClazz = Clazz.builder()
                .className("EX").course(Course.builder().credit(18).build())
                .build();
        existingClazz.setId(40L);
        when(enrollmentRepository.findByStudentId(1L))
                .thenReturn(List.of(existingEnrollment(700L, existingClazz)));

        assertThatThrownBy(() -> registrationService.register(20L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void register_throwsScheduleConflict() {
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.of(openPeriod));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 20L)).thenReturn(false);
        when(clazzRepository.findById(20L)).thenReturn(Optional.of(newClazz));

        // new clazz schedule: Mon day=2 period 1-3
        when(scheduleRepository.findByClazzId(20L))
                .thenReturn(List.of(sched(20L, 2, 1, 3)));

        // existing clazz schedule: Mon day=2 period 2-4 (overlap)
        Clazz existing = Clazz.builder().className("X").course(Course.builder().credit(3).build()).build();
        existing.setId(40L);
        when(enrollmentRepository.findByStudentId(1L))
                .thenReturn(List.of(existingEnrollment(700L, existing)));
        when(scheduleRepository.findByClazzId(40L))
                .thenReturn(List.of(sched(40L, 2, 2, 4)));

        assertThatThrownBy(() -> registrationService.register(20L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void unregister_succeeds() {
        Enrollment existing = existingEnrollment(700L, newClazz);
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.of(openPeriod));
        when(enrollmentRepository.findByStudentIdAndClazzId(1L, 20L)).thenReturn(Optional.of(existing));
        when(lessonProgressRepository.findByEnrollmentId(700L)).thenReturn(new ArrayList<>());

        registrationService.unregister(20L, new UserPrincipal(student));

        verify(enrollmentRepository, times(1)).delete(existing);
    }

    @Test
    void unregister_throwsEnrollmentNotFound() {
        when(periodRepository.findByIsActiveTrue()).thenReturn(Optional.of(openPeriod));
        when(enrollmentRepository.findByStudentIdAndClazzId(1L, 20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.unregister(20L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }
}
