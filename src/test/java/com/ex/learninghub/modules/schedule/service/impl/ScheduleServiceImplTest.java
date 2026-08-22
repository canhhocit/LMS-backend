package com.ex.learninghub.modules.schedule.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.ClassSchedule;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.ClassScheduleRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.schedule.dto.request.ClassScheduleRequest;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class ScheduleServiceImplTest {

    @Mock
    private ClassScheduleRepository scheduleRepository;

    @Mock
    private ClazzRepository clazzRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;


    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private User lecturer;
    private User otherLecturer;
    private User student;
    private User admin;
    private Clazz clazz;

    @BeforeEach
    void setUp() {
        lecturer = User.builder().email("gv@test").role(Role.LECTURER).build();
        lecturer.setId(100L);
        otherLecturer = User.builder().email("gv2@test").role(Role.LECTURER).build();
        otherLecturer.setId(101L);
        student = User.builder().email("sv@test").role(Role.STUDENT).build();
        student.setId(1L);
        admin = User.builder().email("admin").role(Role.ADMIN).build();
        admin.setId(999L);

        Course course = Course.builder().title("Java").credit(3).code("INT123").build();
        course.setId(5L);

        clazz = Clazz.builder().className("INT1001").course(course).build();
        clazz.setId(10L);
        clazz.setLecturer(lecturer);
    }

    private ClassScheduleRequest req(int day, int start, int end, String room) {
        return ClassScheduleRequest.builder()
                .dayOfWeek(day)
                .startPeriod(start)
                .endPeriod(end)
                .room(room)
                .build();
    }

    private ClassSchedule sched(Long id, Integer day, int start, int end, String room) {
        ClassSchedule s = ClassSchedule.builder()
                .clazzId(10L)
                .clazz(clazz)
                .dayOfWeek(day)
                .startPeriod(start)
                .endPeriod(end)
                .room(room)
                .build();
        s.setId(id);
        return s;
    }

    @Test
    void createSchedule_succeeds_forOwnerLecturer() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        when(scheduleRepository.findByClazzId(10L)).thenReturn(new ArrayList<>());
        when(scheduleRepository.save(any())).thenAnswer(inv -> {
            ClassSchedule s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        scheduleService.createSchedule(10L, req(2, 1, 3, "A101"), new UserPrincipal(lecturer));

        verify(scheduleRepository, times(1)).save(any());
    }

    @Test
    void createSchedule_throwsForbidden_whenOtherLecturer() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));

        assertThatThrownBy(() -> scheduleService.createSchedule(
                10L, req(2, 1, 3, "A101"), new UserPrincipal(otherLecturer)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void createSchedule_throwsConflict_whenOverlapInSameClazz() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        // existing schedule occupies Mon day=2 period 1-3
        when(scheduleRepository.findByClazzId(10L)).thenReturn(List.of(sched(99L, 2, 1, 3, "A101")));

        // new request day=2 period 2-4 overlaps existing 1-3
        assertThatThrownBy(() -> scheduleService.createSchedule(
                10L, req(2, 2, 4, "A102"), new UserPrincipal(lecturer)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void createSchedule_noConflict_whenDifferentDay() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        when(scheduleRepository.findByClazzId(10L)).thenReturn(List.of(sched(99L, 2, 1, 3, "A101")));
        when(scheduleRepository.save(any())).thenAnswer(inv -> {
            ClassSchedule s = inv.getArgument(0);
            s.setId(100L);
            return s;
        });

        scheduleService.createSchedule(10L, req(3, 1, 3, "A102"), new UserPrincipal(lecturer));

        verify(scheduleRepository, times(1)).save(any());
    }

    @Test
    void updateSchedule_throwsNotFound_whenMissing() {
        when(scheduleRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduleService.updateSchedule(
                404L, req(2, 1, 3, "A101"), new UserPrincipal(lecturer)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void updateSchedule_throwsForbidden_whenNotOwner() {
        ClassSchedule s = sched(50L, 2, 1, 3, "A101");
        when(scheduleRepository.findById(50L)).thenReturn(Optional.of(s));
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));

        assertThatThrownBy(() -> scheduleService.updateSchedule(
                50L, req(3, 1, 3, "A102"), new UserPrincipal(otherLecturer)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void deleteSchedule_succeeds_forOwner() {
        ClassSchedule s = sched(60L, 2, 1, 3, "A101");
        when(scheduleRepository.findById(60L)).thenReturn(Optional.of(s));
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));

        scheduleService.deleteSchedule(60L, new UserPrincipal(lecturer));

        verify(scheduleRepository, times(1)).delete(s);
    }

    @Test
    void deleteSchedule_succeeds_forAdmin() {
        ClassSchedule s = sched(61L, 2, 1, 3, "A101");
        when(scheduleRepository.findById(61L)).thenReturn(Optional.of(s));
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));

        scheduleService.deleteSchedule(61L, new UserPrincipal(admin));

        verify(scheduleRepository, times(1)).delete(s);
    }

    @Test
    void getSchedulesByClazz_throwsForbidden_whenStudentNotEnrolled() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> scheduleService.getSchedulesByClazz(
                10L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void getMyWeeklySchedule_returnsEmpty_whenStudentNoEnrollment() {
        when(enrollmentRepository.findByStudentId(1L)).thenReturn(new ArrayList<>());

        var list = scheduleService.getMyWeeklySchedule(new UserPrincipal(student));

        assertThat(list).isEmpty();
    }
}
