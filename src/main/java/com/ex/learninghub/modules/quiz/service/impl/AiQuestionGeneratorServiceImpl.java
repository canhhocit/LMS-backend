package com.ex.learninghub.modules.quiz.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.quiz.dto.request.AiGenerateQuestionRequest;
import com.ex.learninghub.modules.quiz.dto.request.QuestionRequest;
import com.ex.learninghub.modules.quiz.dto.response.QuestionResponse;
import com.ex.learninghub.modules.quiz.service.AiQuestionGeneratorService;
import com.ex.learninghub.modules.quiz.service.QuizService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiQuestionGeneratorServiceImpl implements AiQuestionGeneratorService {

    private final QuizService quizService;
    private final AiClientService aiClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<QuestionRequest> generateQuestionsFromText(AiGenerateQuestionRequest request) {
        if (aiClientService.isAiConfigured()) {
            try {
                return generateUsingAiApi(request);
            } catch (Exception e) {
                log.warn("Gọi AI API thất bại, chuyển sang phương thức phân tích quy tắc: {}", e.getMessage());
            }
        }
        return generateFallbackQuestions(request);
    }

    @Override
    public List<QuestionResponse> generateAndAttachToQuiz(Long quizId, AiGenerateQuestionRequest request, Object userPrincipal) {
        UserPrincipal principal = (UserPrincipal) userPrincipal;
        List<QuestionRequest> generatedRequests = generateQuestionsFromText(request);
        List<QuestionResponse> responses = new ArrayList<>();

        for (QuestionRequest qReq : generatedRequests) {
            QuestionResponse created = quizService.createQuestion(quizId, qReq, principal);
            responses.add(created);
        }
        return responses;
    }

    private List<QuestionRequest> generateUsingAiApi(AiGenerateQuestionRequest request) throws Exception {
        String prompt = String.format("""
            Hãy đọc nội dung văn bản/bài giảng dưới đây và tạo đúng %d câu hỏi trắc nghiệm tiếng Việt ở độ khó %s.
            Yêu cầu trả về DUY NHẤT một chuỗi JSON Array nguyên bản (không kèm markdown format ```json), trong đó mỗi phần tử có cấu trúc:
            [
              {
                "questionText": "Nội dung câu hỏi?",
                "optionA": "Lựa chọn A",
                "optionB": "Lựa chọn B",
                "optionC": "Lựa chọn C",
                "optionD": "Lựa chọn D",
                "correctAnswer": "A"
              }
            ]
            
            Nội dung bài giảng:
            %s
            """, request.getNumberOfQuestions(), request.getDifficulty(), request.getContent());

        String rawText = aiClientService.generateContent(prompt);
        if (rawText != null) {
            String text = rawText.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(text, new TypeReference<List<QuestionRequest>>() {});
        }
        return generateFallbackQuestions(request);
    }

    private List<QuestionRequest> generateFallbackQuestions(AiGenerateQuestionRequest request) {
        String[] lines = request.getContent().split("\\r?\\n");
        List<String> validSentences = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 15) {
                validSentences.add(trimmed);
            }
        }

        int count = Math.min(request.getNumberOfQuestions(), Math.max(1, validSentences.size()));
        List<QuestionRequest> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String sentence = validSentences.get(i % validSentences.size());
            QuestionRequest q = new QuestionRequest();
            q.setQuestionText(String.format("Theo bài học: '%s...', phát biểu nào sau đây là ĐÚNG?", 
                    sentence.substring(0, Math.min(sentence.length(), 40))));
            q.setOptionA(sentence);
            q.setOptionB("Khái niệm này hoàn toàn không liên quan đến bài học.");
            q.setOptionC("Nội dung này đã bị bãi bỏ trong quy chuẩn đào tạo mới.");
            q.setOptionD("Tất cả các đáp án trên đều sai.");
            q.setCorrectAnswer("A");
            result.add(q);
        }

        return result;
    }
}
