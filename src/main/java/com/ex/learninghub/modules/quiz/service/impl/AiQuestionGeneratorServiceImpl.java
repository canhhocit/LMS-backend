package com.ex.learninghub.modules.quiz.service.impl;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiQuestionGeneratorServiceImpl implements AiQuestionGeneratorService {

    private final QuizService quizService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.ai.gemini-api-key:}")
    private String geminiApiKey;

    @Value("${app.ai.gemini-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String geminiUrl;

    @Override
    public List<QuestionRequest> generateQuestionsFromText(AiGenerateQuestionRequest request) {
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                return generateUsingGeminiApi(request);
            } catch (Exception e) {
                log.warn("Gọi Gemini API thất bại, chuyển sang phương thức phân tích quy tắc: {}", e.getMessage());
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

    private List<QuestionRequest> generateUsingGeminiApi(AiGenerateQuestionRequest request) throws Exception {
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

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String fullUrl = geminiUrl + "?key=" + geminiApiKey;
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(fullUrl, entity, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            Map<String, Object> respMap = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
            List<?> candidates = (List<?>) respMap.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                Map<?, ?> contentMap = (Map<?, ?>) candidate.get("content");
                List<?> parts = (List<?>) contentMap.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    Map<?, ?> part = (Map<?, ?>) parts.get(0);
                    String text = (String) part.get("text");
                    text = text.replace("```json", "").replace("```", "").trim();
                    return objectMapper.readValue(text, new TypeReference<List<QuestionRequest>>() {});
                }
            }
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
