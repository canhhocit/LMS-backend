package com.ex.learninghub.modules.quiz.service;

import com.ex.learninghub.modules.quiz.dto.request.AiGenerateQuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuestionResponse;

import java.util.List;

public interface AiQuestionGeneratorService {
    
    /**
     * Tự động tạo danh sách câu hỏi trắc nghiệm từ đoạn văn bản/bài giảng bằng AI
     */
    List<QuestionRequest> generateQuestionsFromText(AiGenerateQuestionRequest request);

    /**
     * Tự động sinh và lưu trực tiếp các câu hỏi AI vào bài kiểm tra (Quiz)
     */
    List<QuestionResponse> generateAndAttachToQuiz(Long quizId, AiGenerateQuestionRequest request, Object userPrincipal);
}
