package com.ex.learninghub.modules.enrollment.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.dto.response.ProgressResponse;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.entity.LessonProgress;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceImplTest {

    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private User student;
    private User otherStudent;
    private User lecturer;
    private Clazz clazz;
    private Enrollment enrollment;
    private Lesson lesson;
    private LessonProgress progress;

    @BeforeEach
    void setUp() {
        student = User.builder().email("sv@test.edu.vn").role(Role.STUDENT).build();
        student.setId(1L);

        otherStudent = User.builder().email("sv2@test.edu.vn").role(Role.STUDENT).build();
        otherStudent.setId(2L);

        lecturer = User.builder().email("gv@test.edu.vn").role(Role.LECTURER).build();
        lecturer.setId(100L);

        Course course = Course.builder().title("Java").credit(3).build();
        course.setId(5L);

        clazz = Clazz.builder().className("INT1001").lecturer(lecturer).course(course).build();
        clazz.setId(10L);

        enrollment = Enrollment.builder().student(student).clazz(clazz).build();
        enrollment.setId(50L);

        lesson = Lesson.builder().title("L1").build();
        lesson.setId(20L);

        progress = LessonProgress.builder()
                .enrollment(enrollment)
                .lesson(lesson)
                .isCompleted(false)
                .build();
    }

    private UserPrincipal principalFor(User u) {
        return new UserPrincipal(u);
    }

    @Test
    void markLessonCompleted_succeeds_forEnrolledStudent() {
        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));
        when(lessonRepository.findById(20L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByClazzId(10L)).thenReturn(List.of(lesson));
        when(lessonProgressRepository.findByEnrollmentIdAndLessonId(50L, 20L))
                .thenReturn(Optional.of(progress));
        lenient().when(lessonProgressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        progressService.markLessonCompleted(50L, 20L, principalFor(student));

        assertThat(progress.getIsCompleted()).isTrue();
        assertThat(progress.getCompletedAt()).isNotNull();
    }

    @Test
    void markLessonCompleted_throwsForbidden_whenOtherStudentTries() {
        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> progressService.markLessonCompleted(50L, 20L, principalFor(otherStudent)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void markLessonCompleted_throwsForbidden_whenLessonNotInClazz() {
        Lesson otherClazzLesson = Lesson.builder().title("L-OUT").build();
        otherClazzLesson.setId(99L);

        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));
        when(lessonRepository.findById(99L)).thenReturn(Optional.of(otherClazzLesson));
        when(lessonRepository.findByClazzId(10L)).thenReturn(List.of(lesson)); // doesn't contain 99

        assertThatThrownBy(() -> progressService.markLessonCompleted(50L, 99L, principalFor(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void getProgressByEnrollment_calculatesPercentageCorrectly_forStudent() {
        Lesson l2 = Lesson.builder().title("L2").build();
        l2.setId(21L);
        Lesson l3 = Lesson.builder().title("L3").build();
        l3.setId(22L);

        LessonProgress p1 = LessonProgress.builder()
                .enrollment(enrollment).lesson(lesson).isCompleted(true)
                .completedAt(LocalDateTime.now()).build();
        LessonProgress p2 = LessonProgress.builder()
                .enrollment(enrollment).lesson(l2).isCompleted(true)
                .completedAt(LocalDateTime.now()).build();
        LessonProgress p3 = LessonProgress.builder()
                .enrollment(enrollment).lesson(l3).isCompleted(false).build();

        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));
        when(lessonProgressRepository.findByEnrollmentId(50L)).thenReturn(List.of(p1, p2, p3));

        ProgressResponse response = progressService.getProgressByEnrollment(50L, principalFor(student));

        // 2 of 3 completed → 66.7%
        assertThat(response.getCompletedCount()).isEqualTo(2L);
        assertThat(response.getTotalCount()).isEqualTo(3L);
        assertThat(response.getPercentage()).isEqualTo(66.7);
    }

    @Test
    void getProgressByEnrollment_throwsForbidden_whenAnotherStudentViews() {
        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> progressService.getProgressByEnrollment(50L, principalFor(otherStudent)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void getProgressByEnrollment_adminCanView() {
        User admin = User.builder().email("admin@test").role(Role.ADMIN).build();
        admin.setId(999L);

        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));
        when(lessonProgressRepository.findByEnrollmentId(50L)).thenReturn(List.of(progress));

        ProgressResponse response = progressService.getProgressByEnrollment(50L, principalFor(admin));

        assertThat(response.getEnrollmentId()).isEqualTo(50L);
        assertThat(response.getPercentage()).isEqualTo(0.0);
    }
}
