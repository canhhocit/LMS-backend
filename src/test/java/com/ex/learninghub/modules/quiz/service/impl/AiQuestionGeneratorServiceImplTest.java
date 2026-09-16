package com.ex.learninghub.modules.quiz.service.impl;

import com.ex.learninghub.modules.quiz.dto.request.AiGenerateQuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AiQuestionGeneratorServiceImplTest {

    @Mock
    private QuizService quizService;

    private AiQuestionGeneratorServiceImpl aiQuestionGeneratorService;

    @BeforeEach
    void setUp() {
        aiQuestionGeneratorService = new AiQuestionGeneratorServiceImpl(quizService);
    }

    @Test
    void generateQuestionsFromText_returnsGeneratedQuestions() {
        AiGenerateQuestionRequest request = AiGenerateQuestionRequest.builder()
                .content("Khái niệm lập trình hướng đối tượng OOP bao gồm 4 tính chất chính: Đóng gói, Thừa kế, Đa hình và Trừu tượng.")
                .numberOfQuestions(3)
                .difficulty("MEDIUM")
                .build();

        List<QuestionRequest> questions = aiQuestionGeneratorService.generateQuestionsFromText(request);

        assertThat(questions).isNotNull();
        assertThat(questions).isNotEmpty();
        assertThat(questions.get(0).getCorrectAnswer()).isEqualTo("A");
    }
}
