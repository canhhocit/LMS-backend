package com.ex.learninghub.modules.quiz.dto.response;

import com.ex.learninghub.modules.assessment.entity.Question;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Note: {@code correctAnswer} is intentionally NOT exposed here
 * to prevent students from fetching the answer key via GET endpoints.
 */
@Getter
@Builder
public class QuestionResponse {

    private Long id;
    private Long quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private LocalDateTime createdAt;

    public static QuestionResponse from(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .quizId(question.getQuiz() != null ? question.getQuiz().getId() : null)
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .createdAt(question.getCreatedAt())
                .build();
    }
}
