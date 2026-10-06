package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.content.entity.InVideoQuiz;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ManagedInVideoQuizResponse(
        Long id,
        Long lessonId,
        BigDecimal triggerAtSeconds,
        String questionText,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String correctOption,
        LocalDateTime createdAt
) {
    public static ManagedInVideoQuizResponse from(InVideoQuiz quiz) {
        return new ManagedInVideoQuizResponse(
                quiz.getId(),
                quiz.getLesson().getId(),
                quiz.getTriggerAtSeconds(),
                quiz.getQuestionText(),
                quiz.getOptionA(),
                quiz.getOptionB(),
                quiz.getOptionC(),
                quiz.getOptionD(),
                quiz.getCorrectOption(),
                quiz.getCreatedAt()
        );
    }
}
