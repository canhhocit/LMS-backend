package com.ex.learninghub.modules.quiz.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.entity.Question;
import com.ex.learninghub.modules.assessment.entity.Quiz;
import com.ex.learninghub.modules.assessment.repository.QuestionRepository;
import com.ex.learninghub.modules.assessment.repository.QuizAttemptRepository;
import com.ex.learninghub.modules.assessment.repository.QuizRepository;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.quiz.dto.request.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuizAttemptResponse;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ClazzRepository clazzRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    private User student;
    private UserPrincipal studentPrincipal;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        student = User.builder().email("sv@test.edu.vn").role(Role.STUDENT).build();
        student.setId(1L);
        studentPrincipal = new UserPrincipal(student);

        Clazz clazz = Clazz.builder().className("INT1001").build();
        clazz.setId(10L);

        quiz = Quiz.builder()
                .clazz(clazz)
                .title("Midterm Quiz")
                .totalScore(new BigDecimal("10"))
                .build();
        quiz.setId(100L);
    }

    private Question question(Long id, String correct) {
        Question q = new Question();
        q.setId(id);
        q.setQuestionText("Q" + id);
        q.setCorrectAnswer(correct);
        return q;
    }

    private QuizAttemptRequest attemptRequest() {
        QuizAttemptRequest request = new QuizAttemptRequest();
        request.setAnswers(List.of(
                answer(1L, "A"),
                answer(2L, "B")));
        return request;
    }

    private QuizAttemptRequest.Answer answer(Long questionId, String selected) {
        QuizAttemptRequest.Answer a = new QuizAttemptRequest.Answer();
        a.setQuestionId(questionId);
        a.setSelectedAnswer(selected);
        return a;
    }

    @Test
    void submitAttempt_calculatesScore_correctly() {
        when(quizRepository.findById(100L)).thenReturn(Optional.of(quiz));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 10L)).thenReturn(true);
        when(quizAttemptRepository.existsByQuizIdAndStudentId(100L, 1L)).thenReturn(false);
        // Student has already started the quiz (startedAt set)
        com.ex.learninghub.modules.assessment.entity.QuizAttempt started =
                com.ex.learninghub.modules.assessment.entity.QuizAttempt.builder()
                        .quiz(quiz)
                        .student(student)
                        .startedAt(java.time.LocalDateTime.now().minusMinutes(1))
                        .build();
        when(quizAttemptRepository.findByQuizIdAndStudentId(100L, 1L)).thenReturn(Optional.of(started));
        when(questionRepository.findByQuizId(100L)).thenReturn(List.of(
                question(1L, "A"), question(2L, "B"))); // both correct
        when(quizAttemptRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(inv -> inv.getArgument(0));

        QuizAttemptResponse response = quizService.submitAttempt(100L, 1L, attemptRequest());

        assertThat(response.getCorrectAnswers()).isEqualTo(2);
        assertThat(response.getScore()).isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    void submitAttempt_notEnrolled_throwsUserNotEnrolled() {
        when(quizRepository.findById(100L)).thenReturn(Optional.of(quiz));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> quizService.submitAttempt(100L, 1L, attemptRequest()))
                .isInstanceOf(AppException.class);
    }

    @Test
    void submitAttempt_alreadyAttempted_throwsQuizAlreadyAttempted() {
        when(quizRepository.findById(100L)).thenReturn(Optional.of(quiz));
        when(enrollmentRepository.existsByStudentIdAndClazzId(1L, 10L)).thenReturn(true);
        when(quizAttemptRepository.existsByQuizIdAndStudentId(100L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> quizService.submitAttempt(100L, 1L, attemptRequest()))
                .isInstanceOf(AppException.class);
    }
}