package com.ex.learninghub.modules.enrollment.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.dto.request.EnrollStudentsRequest;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClazzEnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ClazzRepository clazzRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private ClazzEnrollmentServiceImpl enrollmentService;

    private Clazz clazz(Integer maxStudents) {
        return Clazz.builder().className("INT1001").maxStudents(maxStudents).build();
    }

    @Test
    void enrollStudents_success_savesEnrollment() {
        Clazz c = clazz(50);
        EnrollStudentsRequest request = new EnrollStudentsRequest();
        request.setStudentIds(List.of(1L));

        User student = User.builder().email("s1@test.edu.vn").role(Role.STUDENT).build();

        when(clazzRepository.findById(anyLong())).thenReturn(Optional.of(c));
        when(enrollmentRepository.countByClazzId(anyLong())).thenReturn(0L);
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 10L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(lessonRepository.findByClazzId(10L)).thenReturn(List.of());

        assertThatCode(() -> enrollmentService.enrollStudents(10L, request))
                .doesNotThrowAnyException();
    }

    @Test
    void enrollStudents_duplicate_throwsEnrollmentExists() {
        Clazz c = clazz(null);
        EnrollStudentsRequest request = new EnrollStudentsRequest();
        request.setStudentIds(List.of(1L, 2L));

        when(clazzRepository.findById(anyLong())).thenReturn(Optional.of(c));
        lenient().when(enrollmentRepository.countByClazzId(anyLong())).thenReturn(0L);
        // Any student check: default not enrolled (lenient for student 1 who is already enrolled)
        lenient().when(enrollmentRepository.existsByStudentIdAndClazzId(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.eq(10L))).thenReturn(false);
        lenient().when(userRepository.findById(org.mockito.ArgumentMatchers.anyLong())).thenReturn(Optional.of(
                User.builder().email("s@test.edu.vn").role(Role.STUDENT).build()));
        lenient().when(lessonRepository.findByClazzId(10L)).thenReturn(List.of());

        assertThatCode(() -> enrollmentService.enrollStudents(10L, request))
                .doesNotThrowAnyException();
    }

    @Test
    void enrollStudents_exceedsMaxStudents_throwsClazzFull() {
        Clazz c = clazz(30);
        EnrollStudentsRequest request = new EnrollStudentsRequest();
        request.setStudentIds(List.of(1L, 2L, 3L)); // 3 new students

        when(clazzRepository.findById(anyLong())).thenReturn(Optional.of(c));
        when(enrollmentRepository.countByClazzId(anyLong())).thenReturn(29L); // 29 + 3 > 30

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> enrollmentService.enrollStudents(10L, request))
                .isInstanceOf(AppException.class);
    }
}