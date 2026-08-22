package com.ex.learninghub.modules.forum.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.forum.dto.request.ForumPostRequest;
import com.ex.learninghub.modules.forum.dto.response.ForumResponse;
import com.ex.learninghub.modules.forum.entity.ForumPost;
import com.ex.learninghub.modules.forum.repository.ForumCommentRepository;
import com.ex.learninghub.modules.forum.repository.ForumPostRepository;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForumServiceImplTest {

    @Mock
    private ForumPostRepository forumPostRepository;

    @Mock
    private ForumCommentRepository forumCommentRepository;

    @Mock
    private ClazzRepository clazzRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private ForumServiceImpl forumService;

    private User studentEnrolled;
    private User studentNotEnrolled;
    private User lecturerOwner;
    private User otherLecturer;
    private User admin;
    private Clazz clazz;

    @BeforeEach
    void setUp() {
        studentEnrolled = User.builder().email("sv@test.edu.vn").role(Role.STUDENT).build();
        studentEnrolled.setId(1L);

        studentNotEnrolled = User.builder().email("sv2@test.edu.vn").role(Role.STUDENT).build();
        studentNotEnrolled.setId(2L);

        lecturerOwner = User.builder().email("gv@test.edu.vn").role(Role.LECTURER).build();
        lecturerOwner.setId(100L);

        otherLecturer = User.builder().email("gv2@test.edu.vn").role(Role.LECTURER).build();
        otherLecturer.setId(101L);

        admin = User.builder().email("admin@test").role(Role.ADMIN).build();
        admin.setId(999L);

        Course course = Course.builder().title("Java").credit(3).build();
        course.setId(5L);
        clazz = Clazz.builder().className("INT1001").lecturer(lecturerOwner).course(course).build();
        clazz.setId(10L);
    }

    private ForumPostRequest buildRequest() {
        return ForumPostRequest.builder().title("Q").content("A").build();
    }

    @Test
    void createPost_throwsForbidden_whenStudentNotEnrolled() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        when(enrollmentRepository.existsByStudentIdAndClazzId(2L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> forumService.createPost(10L, buildRequest(), new UserPrincipal(studentNotEnrolled)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void createPost_succeeds_forEnrolledStudent() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 10L)).thenReturn(true);
        when(forumPostRepository.save(any())).thenAnswer(inv -> {
            ForumPost p = inv.getArgument(0);
            p.setId(50L);
            return p;
        });

        ForumResponse.Post response = forumService.createPost(10L, buildRequest(), new UserPrincipal(studentEnrolled));

        assertThat(response.getId()).isEqualTo(50L);
        assertThat(response.getTitle()).isEqualTo("Q");
        verify(forumPostRepository, times(1)).save(any());
    }

    @Test
    void createPost_succeeds_forClassLecturer() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));
        when(forumPostRepository.save(any())).thenAnswer(inv -> {
            ForumPost p = inv.getArgument(0);
            p.setId(51L);
            return p;
        });

        ForumResponse.Post response = forumService.createPost(10L, buildRequest(), new UserPrincipal(lecturerOwner));

        assertThat(response.getId()).isEqualTo(51L);
    }

    @Test
    void createPost_throwsForbidden_whenOtherLecturer() {
        when(clazzRepository.findById(10L)).thenReturn(Optional.of(clazz));

        assertThatThrownBy(() -> forumService.createPost(10L, buildRequest(), new UserPrincipal(otherLecturer)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void deletePost_succeeds_whenAuthor() {
        ForumPost post = ForumPost.builder()
                .clazz(clazz).author(studentEnrolled).title("t").content("c").build();
        post.setId(50L);
        when(forumPostRepository.findById(50L)).thenReturn(Optional.of(post));
        lenient().when(forumCommentRepository.findByPostIdOrderByCreatedAtAsc(50L)).thenReturn(new ArrayList<>());

        forumService.deletePost(50L, new UserPrincipal(studentEnrolled));

        verify(forumPostRepository, times(1)).delete(post);
    }

    @Test
    void deletePost_succeeds_whenClassLecturer() {
        ForumPost post = ForumPost.builder()
                .clazz(clazz).author(studentEnrolled).title("t").content("c").build();
        post.setId(51L);
        when(forumPostRepository.findById(51L)).thenReturn(Optional.of(post));
        lenient().when(forumCommentRepository.findByPostIdOrderByCreatedAtAsc(51L)).thenReturn(new ArrayList<>());

        forumService.deletePost(51L, new UserPrincipal(lecturerOwner));

        verify(forumPostRepository, times(1)).delete(post);
    }

    @Test
    void deletePost_succeeds_whenAdmin() {
        ForumPost post = ForumPost.builder()
                .clazz(clazz).author(studentEnrolled).title("t").content("c").build();
        post.setId(52L);
        when(forumPostRepository.findById(52L)).thenReturn(Optional.of(post));
        lenient().when(forumCommentRepository.findByPostIdOrderByCreatedAtAsc(52L)).thenReturn(new ArrayList<>());

        forumService.deletePost(52L, new UserPrincipal(admin));

        verify(forumPostRepository, times(1)).delete(post);
    }

    @Test
    void deletePost_throwsForbidden_whenOtherStudent() {
        ForumPost post = ForumPost.builder()
                .clazz(clazz).author(studentEnrolled).title("t").content("c").build();
        post.setId(53L);
        when(forumPostRepository.findById(53L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> forumService.deletePost(53L, new UserPrincipal(studentNotEnrolled)))
                .isInstanceOf(AppException.class);

        verify(forumPostRepository, times(0)).delete(any());
    }

    @Test
    void deletePost_throwsForbidden_whenOtherLecturer() {
        ForumPost post = ForumPost.builder()
                .clazz(clazz).author(studentEnrolled).title("t").content("c").build();
        post.setId(54L);
        when(forumPostRepository.findById(54L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> forumService.deletePost(54L, new UserPrincipal(otherLecturer)))
                .isInstanceOf(AppException.class);

        verify(forumPostRepository, times(0)).delete(any());
    }
}
