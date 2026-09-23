package com.ex.learninghub.modules.content.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.response.AiLessonSummaryResponse;
import com.ex.learninghub.modules.content.service.AiLessonSummarizerService;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiLessonSummarizerServiceImpl implements AiLessonSummarizerService {

    private final LessonRepository lessonRepository;
    private final AiClientService aiClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(readOnly = true)
    public AiLessonSummaryResponse summarizeLesson(Long lessonId, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        if (aiClientService.isAiConfigured()) {
            try {
                return callAiSummarizerApi(lesson);
            } catch (Exception e) {
                log.warn("Gọi AI Summarizer API thất bại, chuyển sang chế độ phân tích quy tắc: {}", e.getMessage());
            }
        }

        return generateRuleBasedSummary(lesson);
    }

    private AiLessonSummaryResponse callAiSummarizerApi(Lesson lesson) throws Exception {
        String contentText = extractLessonContent(lesson);
        String prompt = String.format("""
            Bạn là một chuyên gia sư phạm. Hãy đọc nội dung bài học '%s' dưới đây và tóm tắt thành các ý chính cốt lõi nhất dể sinh viên ôn tập.
            Nội dung bài học:
            %s
            
            Hãy trả về duy nhất 1 chuỗi JSON hợp lệ (KHÔNG kèm markdown ```json) có cấu trúc:
            {
              "keyTakeaways": ["Ý chính 1", "Ý chính 2", "Ý chính 3"],
              "summaryText": "Đoạn tóm tắt tổng quan nội dung bài học",
              "estimatedStudyMinutes": 15
            }
            """, 
            lesson.getTitle(),
            contentText
        );

        String rawText = aiClientService.generateContent(prompt);
        if (rawText != null) {
            String text = rawText.replace("```json", "").replace("```", "").trim();
            AiLessonSummaryResponse resp = objectMapper.readValue(text, AiLessonSummaryResponse.class);
            resp.setLessonId(lesson.getId());
            resp.setLessonTitle(lesson.getTitle());
            return resp;
        }

        return generateRuleBasedSummary(lesson);
    }

    private AiLessonSummaryResponse generateRuleBasedSummary(Lesson lesson) {
        List<String> keyTakeaways = new ArrayList<>();
        keyTakeaways.add("Nắm vững khái niệm cơ bản và ứng dụng của " + lesson.getTitle());
        keyTakeaways.add("Theo dõi kĩ phần minh họa thực hành và làm bài tập trắc nghiệm củng cố");
        keyTakeaways.add("Ghi nhớ các thuật ngữ chính được trình bày trong bài giảng");

        String summaryText = String.format("Bài học '%s' giới thiệu các nguyên lý cốt lõi, ví dụ minh họa và bài tập vận dụng nhằm giúp sinh viên làm chủ kiến thức học phần.", 
                lesson.getTitle());

        return AiLessonSummaryResponse.builder()
                .lessonId(lesson.getId())
                .lessonTitle(lesson.getTitle())
                .keyTakeaways(keyTakeaways)
                .summaryText(summaryText)
                .estimatedStudyMinutes(20)
                .build();
    }

    private String extractLessonContent(Lesson lesson) {
        StringBuilder sb = new StringBuilder();
        if (lesson.getContent() != null && !lesson.getContent().isBlank()) {
            sb.append(lesson.getContent()).append("\n");
        }
        if (lesson.getAttachmentUrl() != null && !lesson.getAttachmentUrl().isBlank()) {
            sb.append("Tài liệu bài giảng: ").append(lesson.getAttachmentUrl()).append("\n");
        }
        return sb.length() > 0 ? sb.toString() : lesson.getTitle();
    }
}
