package com.ex.learninghub.modules.content.service;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.entity.InVideoQuiz;
import com.ex.learninghub.modules.content.entity.VideoProgress;
import com.ex.learninghub.modules.content.repository.InVideoQuizRepository;
import com.ex.learninghub.modules.content.repository.StudentVideoNoteRepository;
import com.ex.learninghub.modules.content.repository.VideoProgressRepository;
import com.ex.learninghub.modules.course.entity.Chapter;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.ChapterRepository;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoLearningServiceTest {

    @Mock private VideoProgressRepository videoProgressRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private LessonRepository lessonRepository;
    @Mock private ChapterRepository chapterRepository;
    @Mock private ClazzRepository clazzRepository;
    @Mock private InVideoQuizRepository quizRepository;
    @Mock private StudentVideoNoteRepository noteRepository;
    @Mock private UserRepository userRepository;
    @Mock private LessonProgressRepository lessonProgressRepository;

    @InjectMocks private VideoLearningService service;

    @Test
    void upsertProgress_rejectsLessonFromAnotherClass() {
        User student = user(10L, Role.STUDENT);
        Enrollment enrollment = enrollment(student, 100L);
        Lesson lesson = lesson(200L, 1L, 100, "https://cdn.example/video.mp4");
        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));
        when(lessonRepository.findById(200L)).thenReturn(Optional.of(lesson));
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter(1L, 101L)));

        assertThatThrownBy(() -> service.upsertProgress(
                50L, 200L, BigDecimal.ONE, BigDecimal.ONE, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void upsertProgress_marksLessonCompleteOnlyAtEightyPercent() {
        User student = user(10L, Role.STUDENT);
        Enrollment enrollment = enrollment(student, 100L);
        Lesson lesson = lesson(200L, 1L, 100, "https://cdn.example/video.mp4");
        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));
        when(lessonRepository.findById(200L)).thenReturn(Optional.of(lesson));
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter(1L, 100L)));
        when(videoProgressRepository.findByEnrollmentIdAndLessonId(50L, 200L)).thenReturn(Optional.empty());
        when(videoProgressRepository.save(any(VideoProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonProgressRepository.findByEnrollmentIdAndLessonId(50L, 200L)).thenReturn(Optional.empty());
        when(enrollmentRepository.getReferenceById(50L)).thenReturn(enrollment);
        when(lessonRepository.getReferenceById(200L)).thenReturn(lesson);
        when(lessonProgressRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.upsertProgress(
                50L, 200L, new BigDecimal("80"), new BigDecimal("80"), new UserPrincipal(student));

        assertThat(response.completed()).isTrue();
        verify(lessonProgressRepository).save(any());
    }

    @Test
    void studentQuizResponseDoesNotSerializeCorrectAnswer() throws Exception {
        User student = user(10L, Role.STUDENT);
        Lesson lesson = lesson(200L, 1L, 100, "https://cdn.example/video.mp4");
        InVideoQuiz quiz = InVideoQuiz.builder()
                .lesson(lesson)
                .triggerAtSeconds(BigDecimal.TEN)
                .questionText("Question")
                .optionA("A")
                .optionB("B")
                .correctOption("B")
                .build();
        when(lessonRepository.findById(200L)).thenReturn(Optional.of(lesson));
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter(1L, 100L)));
        when(enrollmentRepository.existsByStudentIdAndClazzId(10L, 100L)).thenReturn(true);
        when(quizRepository.findByLessonIdOrderByTriggerAtSecondsAsc(200L)).thenReturn(List.of(quiz));

        String json = new ObjectMapper().writeValueAsString(
                service.getStudentQuizzes(200L, new UserPrincipal(student)));

        assertThat(json).contains("triggerAtSeconds").doesNotContain("correctOption", "correctAnswer");
    }

    @Test
    void studentCannotReadQuizOrNotesForUnenrolledClass() {
        User student = user(10L, Role.STUDENT);
        Lesson lesson = lesson(200L, 1L, 100, "https://cdn.example/video.mp4");
        when(lessonRepository.findById(200L)).thenReturn(Optional.of(lesson));
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter(1L, 100L)));
        when(enrollmentRepository.existsByStudentIdAndClazzId(10L, 100L)).thenReturn(false);

        assertThatThrownBy(() -> service.getStudentQuizzes(200L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
        assertThatThrownBy(() -> service.getNotes(200L, new UserPrincipal(student)))
                .isInstanceOf(AppException.class);
    }

    @Test
    void completionRequiresPersistedServerProgressAtEightyPercent() {
        Lesson lesson = lesson(200L, 1L, 100, "https://cdn.example/video.mp4");
        VideoProgress progress = VideoProgress.builder()
                .lastWatchedSeconds(BigDecimal.valueOf(79))
                .maxWatchedSeconds(BigDecimal.valueOf(79))
                .build();
        when(videoProgressRepository.findByEnrollmentIdAndLessonId(50L, 200L))
                .thenReturn(Optional.of(progress));

        assertThatThrownBy(() -> service.requireCompletionEligible(50L, lesson))
                .isInstanceOf(AppException.class);
    }

    private static User user(Long id, Role role) {
        User user = User.builder().role(role).build();
        user.setId(id);
        return user;
    }

    private static Enrollment enrollment(User student, Long clazzId) {
        Clazz clazz = Clazz.builder().build();
        clazz.setId(clazzId);
        return Enrollment.builder().student(student).clazz(clazz).build();
    }

    private static Chapter chapter(Long id, Long clazzId) {
        Chapter chapter = Chapter.builder().clazzId(clazzId).build();
        chapter.setId(id);
        return chapter;
    }

    private static Lesson lesson(Long id, Long chapterId, Integer duration, String videoUrl) {
        Lesson lesson = Lesson.builder()
                .chapterId(chapterId)
                .duration(duration)
                .videoUrl(videoUrl)
                .build();
        lesson.setId(id);
        return lesson;
    }
}
