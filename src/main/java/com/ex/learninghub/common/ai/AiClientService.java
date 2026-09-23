package com.ex.learninghub.common.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service quản lý tích hợp Spring AI Framework (org.springframework.ai).
 * Sử dụng ChatModel Abstraction để giao tiếp với các mô hình ngôn ngữ (Gemini / OpenAI / Ollama).
 * Hỗ trợ SystemMessage (System Prompt cá nhân hóa) và UserMessage chuẩn Spring AI.
 */
@Slf4j
@Service
public class AiClientService {

    @Value("${app.ai.api-key:${spring.ai.openai.api-key:}}")
    private String apiKey;

    private final ChatModel chatModel;

    public AiClientService(ObjectProvider<ChatModel> chatModelProvider) {
        this.chatModel = chatModelProvider.getIfAvailable();
    }

    public boolean isAiConfigured() {
        return chatModel != null && apiKey != null && !apiKey.isBlank() && !apiKey.contains("mock");
    }

    @io.github.resilience4j.retry.annotation.Retry(name = "aiClient")
    public String generateContent(String promptText) throws Exception {
        if (!isAiConfigured() || chatModel == null) {
            throw new IllegalStateException("Spring AI Framework chưa được cấu hình API Key hợp lệ");
        }
        try {
            Prompt prompt = new Prompt(promptText);
            return chatModel.call(prompt).getResult().getOutput().getText();
        } catch (Exception e) {
            log.error("Lỗi khi gọi Spring AI ChatModel: {}", e.getMessage());
            throw e;
        }
    }

    @io.github.resilience4j.retry.annotation.Retry(name = "aiClient")
    public String generateContent(String systemPrompt, String userPrompt) throws Exception {
        if (!isAiConfigured() || chatModel == null) {
            throw new IllegalStateException("Spring AI Framework chưa được cấu hình API Key hợp lệ");
        }
        try {
            SystemMessage sysMsg = new SystemMessage(systemPrompt != null ? systemPrompt : "Bạn là Trợ lý AI học tập.");
            UserMessage userMsg = new UserMessage(userPrompt != null ? userPrompt : "");
            Prompt prompt = new Prompt(List.of(sysMsg, userMsg));
            return chatModel.call(prompt).getResult().getOutput().getText();
        } catch (Exception e) {
            log.error("Lỗi khi gọi Spring AI ChatModel với System Message: {}", e.getMessage());
            throw e;
        }
    }
}
