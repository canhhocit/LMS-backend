package com.ex.learninghub.common.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClientService {

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.base-url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String baseUrl;

    @Value("${app.ai.model:gemini-1.5-flash}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isAiConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String generateContent(String prompt) throws Exception {
        if (!isAiConfigured()) {
            throw new IllegalStateException("AI API Key chưa được cấu hình");
        }

        String lowerUrl = baseUrl.toLowerCase();
        boolean isOpenAiFormat = lowerUrl.contains("chat/completions") 
                || lowerUrl.contains("openai") 
                || lowerUrl.contains("groq") 
                || lowerUrl.contains("openrouter") 
                || lowerUrl.contains("/v1");

        if (isOpenAiFormat) {
            return callOpenAiApi(prompt);
        } else {
            return callGeminiApi(prompt);
        }
    }

    private String callOpenAiApi(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String targetUrl = baseUrl.endsWith("/chat/completions") ? baseUrl : (baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions");

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(targetUrl, entity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            Map<String, Object> respMap = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
            List<?> choices = (List<?>) respMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) choice.get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
        }
        throw new RuntimeException("OpenAI-compatible API không trả về phản hồi hợp lệ");
    }

    private String callGeminiApi(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String targetUrl;
        if (baseUrl.contains(":generateContent")) {
            targetUrl = baseUrl + "?key=" + apiKey;
        } else if (baseUrl.endsWith("/")) {
            targetUrl = baseUrl + model + ":generateContent?key=" + apiKey;
        } else {
            targetUrl = baseUrl + "/" + model + ":generateContent?key=" + apiKey;
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(targetUrl, entity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            Map<String, Object> respMap = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
            List<?> candidates = (List<?>) respMap.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                Map<?, ?> contentMap = (Map<?, ?>) candidate.get("content");
                if (contentMap != null) {
                    List<?> parts = (List<?>) contentMap.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        Map<?, ?> part = (Map<?, ?>) parts.get(0);
                        return (String) part.get("text");
                    }
                }
            }
        }
        throw new RuntimeException("Gemini API không trả về phản hồi hợp lệ");
    }
}
