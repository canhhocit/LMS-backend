package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.content.entity.InVideoQuiz;

import java.math.BigDecimal;

public record StudentInVideoQuizResponse(
        Long id,
        Long lessonId,
        BigDecimal triggerAtSeconds,
        String questionText,
        String optionA,
        String optionB,
        String optionC,
        String optionD
) {
    public static StudentInVideoQuizResponse from(InVideoQuiz quiz) {
        return new StudentInVideoQuizResponse(
                quiz.getId(),
                quiz.getLesson().getId(),
                quiz.getTriggerAtSeconds(),
                quiz.getQuestionText(),
                quiz.getOptionA(),
                quiz.getOptionB(),
                quiz.getOptionC(),
                quiz.getOptionD()
        );
    }
}
