package com.ex.learninghub.modules.content.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.response.AiLessonSummaryResponse;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiLessonSummarizerServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private AiClientService aiClientService;

    @InjectMocks
    private AiLessonSummarizerServiceImpl aiLessonSummarizerService;

    private Lesson lesson;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        lenient().when(aiClientService.isAiConfigured()).thenReturn(false);

        User user = User.builder().email("student@test.com").build();
        user.setId(100L);
        principal = new UserPrincipal(user);

        lesson = Lesson.builder()
                .title("Bài 1: Tổng quan về lập trình Hướng đối tượng")
                .content("Học về Class, Object, Encapsulation, Inheritance, Polymorphism")
                .build();
        lesson.setId(20L);
    }

    @Test
    void summarizeLesson_ruleBasedFallback_returnsValidSummary() {
        when(lessonRepository.findById(20L)).thenReturn(Optional.of(lesson));

        AiLessonSummaryResponse response = aiLessonSummarizerService.summarizeLesson(20L, principal);

        assertThat(response).isNotNull();
        assertThat(response.getLessonId()).isEqualTo(20L);
        assertThat(response.getLessonTitle()).contains("Tổng quan");
        assertThat(response.getKeyTakeaways()).hasSize(3);
    }

    @Test
    void summarizeLesson_throwsException_whenLessonNotFound() {
        when(lessonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiLessonSummarizerService.summarizeLesson(999L, principal))
                .isInstanceOf(AppException.class);
    }
}
