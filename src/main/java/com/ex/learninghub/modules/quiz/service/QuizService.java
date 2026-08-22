package com.ex.learninghub.modules.quiz.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuizRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuestionResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizAttemptResponse;
import com.ex.learninghub.modules.quiz.dto.response.QuizResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizService {

    // Quiz CRUD
    QuizResponse createQuiz(Long classId, QuizRequest request, UserPrincipal userPrincipal);
    QuizResponse getQuizById(Long quizId);
    List<QuizResponse> getQuizzesByClassId(Long classId);
    QuizResponse updateQuiz(Long quizId, QuizRequest request, UserPrincipal userPrincipal);
    void deleteQuiz(Long quizId, UserPrincipal userPrincipal);

    // Question CRUD
    QuestionResponse createQuestion(Long quizId, QuestionRequest request, UserPrincipal userPrincipal);
    List<QuestionResponse> getQuestionsByQuizId(Long quizId);
    QuestionResponse updateQuestion(Long questionId, QuestionRequest request, UserPrincipal userPrincipal);
    void deleteQuestion(Long questionId, UserPrincipal userPrincipal);

    // Quiz Attempt
    LocalDateTime startQuiz(Long quizId, UserPrincipal userPrincipal);
    QuizAttemptResponse submitAttempt(Long quizId, Long studentId, QuizAttemptRequest request);
}