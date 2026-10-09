package com.ex.learninghub.common.ai.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.ai.dto.RagIngestRequest;
import com.ex.learninghub.common.ai.dto.RagQueryRequest;
import com.ex.learninghub.common.ai.dto.RagQueryResponse;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.common.ai.service.RagService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    private final AiClientService aiClientService;

    /** In-Memory Chunked Document Store với tagging metadata (sẵn sàng nâng cấp kết nối PgVectorStore) */
    private final List<DocumentChunk> documentStore = new java.util.concurrent.CopyOnWriteArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentChunk {
        private String id;
        private Long clazzId;
        private Long lessonId;
        private String sourceName;
        private String content;
        private LocalDateTime ingestedAt;
    }

    @Override
    public void ingestDocument(RagIngestRequest request, UserPrincipal principal) {
        if (request == null || request.getDocumentContent() == null || request.getDocumentContent().isBlank()) {
            throw new AppException(ErrorCode.KEY_INVALID);
        }

        // Chunking document content by lines/paragraphs
        String[] paragraphs = request.getDocumentContent().split("\\r?\\n\\s*\\r?\\n|(?<=\\.)\\s+");
        List<String> validChunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        for (String p : paragraphs) {
            String trimmed = p.trim();
            if (trimmed.length() < 10) continue;

            if (currentChunk.length() + trimmed.length() > 500) {
                validChunks.add(currentChunk.toString());
                currentChunk = new StringBuilder(trimmed);
            } else {
                if (currentChunk.length() > 0) currentChunk.append(" ");
                currentChunk.append(trimmed);
            }
        }
        if (currentChunk.length() > 0) {
            validChunks.add(currentChunk.toString());
        }

        for (int i = 0; i < validChunks.size(); i++) {
            DocumentChunk chunk = DocumentChunk.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .clazzId(request.getClazzId())
                    .lessonId(request.getLessonId())
                    .sourceName(request.getSourceName() != null ? request.getSourceName() : "Bài giảng LMS")
                    .content(validChunks.get(i))
                    .ingestedAt(LocalDateTime.now())
                    .build();
            documentStore.add(chunk);
        }

        log.info("RAG Ingestion: Đã nạp thành công {} đoạn văn bản từ nguồn '{}' cho ClassId={}", 
                validChunks.size(), request.getSourceName(), request.getClazzId());
    }

    @Override
    public RagQueryResponse queryCourseMaterials(RagQueryRequest request, UserPrincipal principal) {
        if (request == null || request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new AppException(ErrorCode.KEY_INVALID);
        }

        int topK = request.getTopK() != null && request.getTopK() > 0 ? request.getTopK() : 3;
        List<DocumentChunk> matchedChunks = retrieveRelevantChunks(request.getQuestion(), request.getClazzId(), request.getLessonId(), topK);

        List<String> sources = matchedChunks.stream()
                .map(c -> String.format("[%s] %s", c.getSourceName(), c.getContent()))
                .toList();

        if (aiClientService.isAiConfigured()) {
            try {
                String prompt = buildAugmentedPrompt(request.getQuestion(), matchedChunks);
                String aiAnswer = aiClientService.generateContent(prompt);
                return RagQueryResponse.builder()
                        .question(request.getQuestion())
                        .answer(aiAnswer)
                        .retrievedSources(sources)
                        .isRagUsed(true)
                        .retrievedCount(matchedChunks.size())
                        .build();
            } catch (Exception e) {
                log.warn("Gọi AI RAG API thất bại, chuyển sang chế độ phản hồi quy tắc RAG: {}", e.getMessage());
            }
        }

        return generateRuleBasedRagResponse(request.getQuestion(), matchedChunks, sources);
    }

    private List<DocumentChunk> retrieveRelevantChunks(String question, Long clazzId, Long lessonId, int topK) {
        String queryLower = question.toLowerCase();
        String[] keywords = queryLower.split("\\s+");

        List<DocumentChunk> candidates = documentStore.stream()
                .filter(c -> clazzId == null || clazzId.equals(c.getClazzId()))
                .filter(c -> lessonId == null || lessonId.equals(c.getLessonId()))
                .toList();

        if (candidates.isEmpty()) {
            // If store is empty, fallback to searching all candidates
            candidates = documentStore;
        }

        // Simple Keyword Relevance Scoring (Simulating Cosine Similarity for fallback mode)
        List<Map.Entry<DocumentChunk, Integer>> scored = new ArrayList<>();
        for (DocumentChunk chunk : candidates) {
            String contentLower = chunk.getContent().toLowerCase();
            int score = 0;
            for (String kw : keywords) {
                if (kw.length() > 2 && contentLower.contains(kw)) {
                    score += 2;
                }
            }
            if (score > 0 || scored.size() < topK) {
                scored.add(Map.entry(chunk, score));
            }
        }

        scored.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        return scored.stream()
                .limit(topK)
                .map(Map.Entry::getKey)
                .toList();
    }

    private String buildAugmentedPrompt(String question, List<DocumentChunk> chunks) {
        StringBuilder contextBuilder = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk c = chunks.get(i);
            contextBuilder.append(String.format("[%d] (Nguồn: %s)\n%s\n\n", i + 1, c.getSourceName(), c.getContent()));
        }

        return String.format("""
            Bạn là Trợ lý Học tập AI dành cho môn học. Dưới đây là các đoạn văn bản tài liệu bài giảng được trích xuất từ hệ thống:
            
            %s
            
            Dựa CHÍNH XÁC vào các đoạn tài liệu tham khảo ở trên, hãy trả lời câu hỏi sau của sinh viên:
            "%s"
            
            Yêu cầu: Trả lời ngắn gọn, chính xác, súc tích và ghi rõ số thứ tự tài liệu tham khảo [1], [2] nếu áp dụng.
            """, 
            contextBuilder.toString(),
            question
        );
    }

    private RagQueryResponse generateRuleBasedRagResponse(String question, List<DocumentChunk> chunks, List<String> sources) {
        String answerText;
        if (!chunks.isEmpty()) {
            answerText = String.format("Dựa trên %d đoạn tài liệu trích xuất từ bài giảng, nội dung liên quan tới '%s' là:\n%s", 
                    chunks.size(), question, chunks.get(0).getContent());
        } else {
            answerText = String.format("Hệ thống RAG đã tiếp nhận câu hỏi '%s'. Hiện tại tài liệu bài giảng chưa được nạp hoặc chưa tìm thấy đoạn trùng khớp.", question);
        }

        return RagQueryResponse.builder()
                .question(question)
                .answer(answerText)
                .retrievedSources(sources)
                .isRagUsed(!chunks.isEmpty())
                .retrievedCount(chunks.size())
                .build();
    }
}
