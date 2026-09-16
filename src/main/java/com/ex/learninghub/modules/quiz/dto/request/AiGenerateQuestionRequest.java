package com.ex.learninghub.modules.quiz.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiGenerateQuestionRequest {

    @NotBlank(message = "Nội dung văn bản/bài giảng không được để trống")
    private String content;

    @Min(value = 1, message = "Số lượng câu hỏi tối thiểu là 1")
    @Max(value = 20, message = "Số lượng câu hỏi tối đa là 20")
    @Builder.Default
    private Integer numberOfQuestions = 5;

    @Builder.Default
    private String difficulty = "MEDIUM";
}
