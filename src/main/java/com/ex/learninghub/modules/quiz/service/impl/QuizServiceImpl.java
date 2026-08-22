package com.ex.learninghub.modules.quiz.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.assessment.entity.Question;
import com.ex.learninghub.modules.assessment.entity.Quiz;
import com.ex.learninghub.modules.assessment.entity.QuizAttempt;
import com.ex.learninghub.modules.assessment.repository.QuestionRepository;
import com.ex.learninghub.modules.assessment.repository.QuizAttemptRepository;
import com.ex.learninghub.modules.assessment.repository.QuizRepository;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuestionResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizAttemptResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizResponse;
import com.ex.learninghub.modules.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ClazzRepository clazzRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    // ==================== Quiz CRUD ====================

    @Override
    @Transactional
    public QuizResponse createQuiz(Long classId, QuizRequest request) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        Quiz quiz = new Quiz();
        quiz.setClazz(clazz);
        quiz.setTitle(request.getTitle());
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setTotalScore(request.getTotalScore());

        Quiz saved = quizRepository.save(quiz);
        return QuizResponse.from(saved);
    }

    @Override
    public QuizResponse getQuizById(Long quizId) {
        Quiz quiz = findQuizOrThrow(quizId);
        return QuizResponse.from(quiz);
    }

    @Override
    public List<QuizResponse> getQuizzesByClassId(Long classId) {
        return quizRepository.findByClazzId(classId).stream()
                .map(QuizResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long quizId, QuizRequest request) {
        Quiz existing = findQuizOrThrow(quizId);
        existing.setTitle(request.getTitle());
        existing.setDurationMinutes(request.getDurationMinutes());
        existing.setTotalScore(request.getTotalScore());

        Quiz saved = quizRepository.save(existing);
        return QuizResponse.from(saved);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = findQuizOrThrow(quizId);
        quizRepository.delete(quiz);
    }

    // ==================== Question CRUD ====================

    @Override
    @Transactional
    public QuestionResponse createQuestion(Long quizId, QuestionRequest request) {
        Quiz quiz = findQuizOrThrow(quizId);

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());

        Question saved = questionRepository.save(question);
        return QuestionResponse.from(saved);
    }

    @Override
    public List<QuestionResponse> getQuestionsByQuizId(Long quizId) {
        // Verify quiz exists
        findQuizOrThrow(quizId);
        return questionRepository.findByQuizId(quizId).stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        Question existing = findQuestionOrThrow(questionId);
        existing.setQuestionText(request.getQuestionText());
        existing.setOptionA(request.getOptionA());
        existing.setOptionB(request.getOptionB());
        existing.setOptionC(request.getOptionC());
        existing.setOptionD(request.getOptionD());
        existing.setCorrectAnswer(request.getCorrectAnswer());

        Question saved = questionRepository.save(existing);
        return QuestionResponse.from(saved);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = findQuestionOrThrow(questionId);
        questionRepository.delete(question);
    }

    // ==================== Quiz Attempt ====================

    @Override
    @Transactional
    public QuizAttemptResponse submitAttempt(Long quizId, Long studentId, QuizAttemptRequest request) {
        Quiz quiz = findQuizOrThrow(quizId);
        List<Question> questions = questionRepository.findByQuizId(quizId);

        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int correctAnswers = 0;
        int totalQuestions = questions.size();

        if (request.getAnswers() != null) {
            for (QuizAttemptRequest.Answer answer : request.getAnswers()) {
                Question question = questionMap.get(answer.getQuestionId());
                if (question != null && question.getCorrectAnswer() != null
                        && question.getCorrectAnswer().equalsIgnoreCase(answer.getSelectedAnswer())) {
                    correctAnswers++;
                }
            }
        }

        BigDecimal score = BigDecimal.ZERO;
        if (totalQuestions > 0 && quiz.getTotalScore() != null) {
            score = BigDecimal.valueOf(correctAnswers)
                    .multiply(quiz.getTotalScore())
                    .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP);
        }

        // Save the attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        com.ex.learninghub.modules.user.entity.User student = new com.ex.learninghub.modules.user.entity.User();
        student.setId(studentId);
        attempt.setStudent(student);
        attempt.setScore(score);
        attempt.setSubmittedAt(LocalDateTime.now());
        quizAttemptRepository.save(attempt);

        return QuizAttemptResponse.from(attempt, totalQuestions, correctAnswers, quiz.getTotalScore());
    }

    // ==================== Private helpers ====================

    private Quiz findQuizOrThrow(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
    }

    private Question findQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
    }
}