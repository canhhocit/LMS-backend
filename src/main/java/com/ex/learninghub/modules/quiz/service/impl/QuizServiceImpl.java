package com.ex.learninghub.modules.quiz.service.impl;

import com.ex.learninghub.common.exception.BusinessException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.assessment.entity.Question;
import com.ex.learninghub.modules.assessment.entity.Quiz;
import com.ex.learninghub.modules.assessment.repository.QuestionRepository;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.quiz.dto.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.QuizAttemptResponse;
import com.ex.learninghub.modules.quiz.repository.QuizRepository;
import com.ex.learninghub.modules.quiz.service.QuizService;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ClazzRepository clazzRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Quiz createQuiz(Long classId, Quiz quiz) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAZZ_NOT_FOUND));
        quiz.setClazz(clazz);
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }

    @Override
    public List<Quiz> getQuizzesByClassId(Long classId) {
        return quizRepository.findByClazzId(classId);
    }

    @Override
    @Transactional
    public Quiz updateQuiz(Long quizId, Quiz quiz) {
        Quiz existing = getQuizById(quizId);
        existing.setTitle(quiz.getTitle());
        existing.setDurationMinutes(quiz.getDurationMinutes());
        existing.setTotalScore(quiz.getTotalScore());
        return quizRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = getQuizById(quizId);
        quizRepository.delete(quiz);
    }

    @Override
    @Transactional
    public Question createQuestion(Long quizId, Question question) {
        Quiz quiz = getQuizById(quizId);
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }

    @Override
    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    @Override
    @Transactional
    public Question updateQuestion(Long questionId, Question question) {
        Question existing = getQuestionById(questionId);
        existing.setQuestionText(question.getQuestionText());
        existing.setOptionA(question.getOptionA());
        existing.setOptionB(question.getOptionB());
        existing.setOptionC(question.getOptionC());
        existing.setOptionD(question.getOptionD());
        existing.setCorrectAnswer(question.getCorrectAnswer());
        return questionRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = getQuestionById(questionId);
        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public QuizAttemptResponse submitAttempt(Long quizId, QuizAttemptRequest request) {
        Quiz quiz = getQuizById(quizId);
        List<Question> questions = questionRepository.findByQuizId(quizId);

        // Map questions by ID for easy lookup
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int correctAnswers = 0;
        int totalQuestions = questions.size();

        // Calculate score
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

        return QuizAttemptResponse.builder()
                .quizId(quizId)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .score(score)
                .totalScore(quiz.getTotalScore())
                .build();
    }
}