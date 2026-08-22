package com.ex.learninghub.modules.quiz.service;

import com.ex.learninghub.modules.assessment.entity.Question;
import com.ex.learninghub.modules.assessment.entity.Quiz;
import com.ex.learninghub.modules.quiz.dto.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.QuizAttemptResponse;

import java.util.List;
import java.util.Map;

public interface QuizService {
    // Quiz CRUD
    Quiz createQuiz(Long classId, Quiz quiz);
    Quiz getQuizById(Long quizId);
    List<Quiz> getQuizzesByClassId(Long classId);
    Quiz updateQuiz(Long quizId, Quiz quiz);
    void deleteQuiz(Long quizId);

    // Question CRUD
    Question createQuestion(Long quizId, Question question);
    Question getQuestionById(Long questionId);
    List<Question> getQuestionsByQuizId(Long quizId);
    Question updateQuestion(Long questionId, Question question);
    void deleteQuestion(Long questionId);

    // Quiz Attempt
    QuizAttemptResponse submitAttempt(Long quizId, QuizAttemptRequest request);
}