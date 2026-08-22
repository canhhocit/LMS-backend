package com.ex.learninghub.modules.quiz.service;

import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuestionResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizAttemptResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizResponse;

import java.util.List;

public interface QuizService {

    // Quiz CRUD
    QuizResponse createQuiz(Long classId, QuizRequest request);
    QuizResponse getQuizById(Long quizId);
    List<QuizResponse> getQuizzesByClassId(Long classId);
    QuizResponse updateQuiz(Long quizId, QuizRequest request);
    void deleteQuiz(Long quizId);

    // Question CRUD
    QuestionResponse createQuestion(Long quizId, QuestionRequest request);
    List<QuestionResponse> getQuestionsByQuizId(Long quizId);
    QuestionResponse updateQuestion(Long questionId, QuestionRequest request);
    void deleteQuestion(Long questionId);

    // Quiz Attempt
    QuizAttemptResponse submitAttempt(Long quizId, Long studentId, QuizAttemptRequest request);
}