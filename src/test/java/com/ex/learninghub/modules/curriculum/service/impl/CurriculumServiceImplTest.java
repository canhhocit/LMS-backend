package com.ex.learninghub.modules.curriculum.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.curriculum.dto.request.CurriculumCourseRequest;
import com.ex.learninghub.modules.curriculum.dto.request.PrerequisiteRequest;
import com.ex.learninghub.modules.curriculum.entity.CoursePrerequisite;
import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import com.ex.learninghub.modules.curriculum.entity.CurriculumCourse;
import com.ex.learninghub.modules.curriculum.repository.CoursePrerequisiteRepository;
import com.ex.learninghub.modules.curriculum.repository.CurriculumCourseRepository;
import com.ex.learninghub.modules.curriculum.repository.CurriculumRepository;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculumServiceImplTest {

    @Mock
    private CurriculumRepository curriculumRepository;
    @Mock
    private CurriculumCourseRepository curriculumCourseRepository;
    @Mock
    private CoursePrerequisiteRepository prerequisiteRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private CurriculumServiceImpl curriculumService;

    private Curriculum curriculum;
    private Course course1, course2, course3;

    @BeforeEach
    void setUp() {
        curriculum = Curriculum.builder()
                .name("CNTT K2024")
                .faculty("Khoa CNTT")
                .academicYear("2024-2025")
                .isActive(true)
                .build();
        curriculum.setId(1L);

        course1 = Course.builder().code("INT101").title("Java").credit(3).build();
        course1.setId(10L);
        course2 = Course.builder().code("INT102").title("DB").credit(3).build();
        course2.setId(11L);
        course3 = Course.builder().code("INT103").title("Web").credit(2).build();
        course3.setId(12L);

        // @Value không được inject bởi @InjectMocks
        ReflectionTestUtils.setField(curriculumService, "passScore", new BigDecimal("5.0"));
    }

    // ===== addCourseToCurriculum =====
    @Test
    void addCourseToCurriculum_succeeds() {
        when(curriculumRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course1));
        when(curriculumCourseRepository.findByCurriculumIdAndCourseId(1L, 10L)).thenReturn(Optional.empty());
        when(curriculumCourseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var resp = curriculumService.addCourseToCurriculum(1L,
                CurriculumCourseRequest.builder().courseId(10L).semesterNo(1).isRequired(true).build());

        assertThat(resp.getCourseId()).isEqualTo(10L);
        assertThat(resp.getSemesterNo()).isEqualTo(1);
        assertThat(resp.getIsRequired()).isTrue();
        verify(curriculumCourseRepository).save(any(CurriculumCourse.class));
    }

    @Test
    void addCourseToCurriculum_throws_whenCurriculumNotFound() {
        when(curriculumRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> curriculumService.addCourseToCurriculum(99L,
                CurriculumCourseRequest.builder().courseId(10L).semesterNo(1).build()))
                .isInstanceOf(AppException.class);
    }

    @Test
    void addCourseToCurriculum_throws_whenCourseNotFound() {
        when(curriculumRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> curriculumService.addCourseToCurriculum(1L,
                CurriculumCourseRequest.builder().courseId(99L).semesterNo(1).build()))
                .isInstanceOf(AppException.class);
    }

    // ===== addPrerequisite =====
    @Test
    void addPrerequisite_succeeds() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course1));
        when(courseRepository.findById(11L)).thenReturn(Optional.of(course2));
        when(prerequisiteRepository.existsByCourseIdAndPrerequisiteCourseId(10L, 11L)).thenReturn(false);
        when(prerequisiteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var resp = curriculumService.addPrerequisite(10L, new PrerequisiteRequest(11L));

        assertThat(resp.getCourseId()).isEqualTo(10L);
        assertThat(resp.getPrerequisiteCourseId()).isEqualTo(11L);
        assertThat(resp.getPrerequisiteCourseCode()).isEqualTo("INT102");
    }

    @Test
    void addPrerequisite_throws_selfReference() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course1));
        assertThatThrownBy(() -> curriculumService.addPrerequisite(10L, new PrerequisiteRequest(10L)))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PREREQUISITE_NOT_MET);
    }

    @Test
    void addPrerequisite_throws_duplicate() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course1));
        when(courseRepository.findById(11L)).thenReturn(Optional.of(course2));
        when(prerequisiteRepository.existsByCourseIdAndPrerequisiteCourseId(10L, 11L)).thenReturn(true);

        assertThatThrownBy(() -> curriculumService.addPrerequisite(10L, new PrerequisiteRequest(11L)))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PREREQUISITE_NOT_MET);
    }

    // ===== checkMissingPrerequisites =====
    @Test
    void checkMissingPrerequisites_returnsEmpty_whenNoPrereqs() {
        // prereq rỗng → service trả về ngay, không gọi gradeRepository
        when(prerequisiteRepository.findByCourseId(10L)).thenReturn(List.of());

        List<Long> missing = curriculumService.checkMissingPrerequisites(10L, principal(1L));

        assertThat(missing).isEmpty();
    }

    @Test
    void checkMissingPrerequisites_returnsEmpty_whenAllMet() {
        CoursePrerequisite p1 = CoursePrerequisite.builder().courseId(10L).prerequisiteCourseId(11L).build();
        p1.setId(1L);
        when(prerequisiteRepository.findByCourseId(10L)).thenReturn(List.of(p1));
        when(gradeRepository.findPassedCourseIds(1L, new BigDecimal("5.0"))).thenReturn(List.of(11L));

        List<Long> missing = curriculumService.checkMissingPrerequisites(10L, principal(1L));

        assertThat(missing).isEmpty();
    }

    @Test
    void checkMissingPrerequisites_returnsMissing_whenNotMet() {
        CoursePrerequisite p1 = CoursePrerequisite.builder().courseId(10L).prerequisiteCourseId(11L).build();
        p1.setId(1L);
        CoursePrerequisite p2 = CoursePrerequisite.builder().courseId(10L).prerequisiteCourseId(12L).build();
        p2.setId(2L);
        when(prerequisiteRepository.findByCourseId(10L)).thenReturn(List.of(p1, p2));
        when(gradeRepository.findPassedCourseIds(1L, new BigDecimal("5.0"))).thenReturn(List.of(11L));

        List<Long> missing = curriculumService.checkMissingPrerequisites(10L, principal(1L));

        assertThat(missing).containsExactly(12L);
    }

    private UserPrincipal principal(long id) {
        User u = User.builder().build();
        u.setId(id);
        return new UserPrincipal(u);
    }
}
