package com.ex.learninghub.modules.quiz.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizAttemptRequest {
    private List<Answer> answers;

    @Data
    public static class Answer {
        private Long questionId;
        private String selectedAnswer; // A, B, C, or D
    }
}