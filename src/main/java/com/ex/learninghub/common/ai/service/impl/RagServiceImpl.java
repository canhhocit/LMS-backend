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
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    private final AiClientService aiClientService;

    /** In-Memory Chunked Document Store với tagging metadata */
    private final List<DocumentChunk> documentStore = new CopyOnWriteArrayList<>();

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

        // Improved chunking: Overlap 100 chars to keep context
        String content = request.getDocumentContent().replaceAll("\\s+", " ");
        List<String> validChunks = new ArrayList<>();
        int chunkSize = 600;
        int overlap = 100;
        
        int start = 0;
        while (start < content.length()) {
            int end = Math.min(start + chunkSize, content.length());
            if (end < content.length()) {
                int lastPeriod = content.lastIndexOf(". ", end);
                if (lastPeriod > start + chunkSize / 2) {
                    end = lastPeriod + 1;
                }
            }
            validChunks.add(content.substring(start, end).trim());
            start = end - overlap;
            if (start < 0) start = 0;
            if (end == content.length()) break;
        }

        for (String chunkText : validChunks) {
            DocumentChunk chunk = DocumentChunk.builder()
                    .id(UUID.randomUUID().toString())
                    .clazzId(request.getClazzId())
                    .lessonId(request.getLessonId())
                    .sourceName(request.getSourceName() != null ? request.getSourceName() : "Tài liệu học tập")
                    .content(chunkText)
                    .ingestedAt(LocalDateTime.now())
                    .build();
            documentStore.add(chunk);
        }

        log.info("RAG Ingestion: Đã nạp thành công {} đoạn văn bản từ '{}' cho ClassId={}", 
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
                .map(c -> String.format("[%s]", c.getSourceName()))
                .distinct()
                .toList();

        if (aiClientService.isAiConfigured() && !matchedChunks.isEmpty()) {
            try {
                String systemPrompt = "Bạn là Trợ lý AI của hệ thống LMS. Nhiệm vụ của bạn là trả lời câu hỏi dựa CHÍNH XÁC vào dữ liệu tài liệu được cung cấp. Không bịa đặt thông tin. Nếu tài liệu không đủ thông tin, hãy nói rõ 'Tài liệu hiện tại không đề cập đến vấn đề này'.";
                String prompt = buildAugmentedPrompt(request.getQuestion(), matchedChunks);
                
                long startTime = System.currentTimeMillis();
                String aiAnswer = aiClientService.generateContent(systemPrompt, prompt);
                long duration = System.currentTimeMillis() - startTime;
                
                log.info("RAG Generation: {}ms, Retrieved: {} chunks", duration, matchedChunks.size());

                return RagQueryResponse.builder()
                        .question(request.getQuestion())
                        .answer(aiAnswer)
                        .retrievedSources(sources)
                        .isRagUsed(true)
                        .retrievedCount(matchedChunks.size())
                        .build();
            } catch (Exception e) {
                log.warn("Gọi AI RAG API thất bại: {}", e.getMessage());
            }
        }

        return generateRuleBasedRagResponse(request.getQuestion(), matchedChunks, sources);
    }

    private List<DocumentChunk> retrieveRelevantChunks(String question, Long clazzId, Long lessonId, int topK) {
        String queryLower = question.toLowerCase();
        Set<String> queryWords = Arrays.stream(queryLower.split("[\\s\\p{Punct}]+"))
                .filter(w -> w.length() > 2)
                .collect(Collectors.toSet());

        List<DocumentChunk> candidates = documentStore.stream()
                .filter(c -> clazzId == null || clazzId.equals(c.getClazzId()))
                .filter(c -> lessonId == null || lessonId.equals(c.getLessonId()))
                .toList();

        if (candidates.isEmpty()) candidates = documentStore;

        // Improved TF-like Scoring
        List<Map.Entry<DocumentChunk, Double>> scored = new ArrayList<>();
        for (DocumentChunk chunk : candidates) {
            String contentLower = chunk.getContent().toLowerCase();
            double score = 0;
            for (String w : queryWords) {
                int index = 0;
                while ((index = contentLower.indexOf(w, index)) != -1) {
                    score += 1.0;
                    index += w.length();
                }
            }
            if (score > 0) {
                scored.add(Map.entry(chunk, score));
            }
        }

        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return scored.stream()
                .limit(topK)
                .map(Map.Entry::getKey)
                .toList();
    }

    private String buildAugmentedPrompt(String question, List<DocumentChunk> chunks) {
        StringBuilder contextBuilder = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk c = chunks.get(i);
            contextBuilder.append(String.format("TÀI LIỆU [%d] (Nguồn: %s):\n%s\n\n", i + 1, c.getSourceName(), c.getContent()));
        }

        return String.format(
            "CÂU HỎI CỦA NGƯỜI DÙNG: \"%s\"\n\n---\nNGỮ CẢNH TRÍCH XUẤT TỪ HỆ THỐNG:\n%s\n---\nHãy trả lời câu hỏi dựa trên Ngữ cảnh trên. Đừng quên trích dẫn nguồn (VD: Theo tài liệu [1]).", 
            question, contextBuilder.toString()
        );
    }

    private RagQueryResponse generateRuleBasedRagResponse(String question, List<DocumentChunk> chunks, List<String> sources) {
        String answerText;
        if (!chunks.isEmpty()) {
            answerText = String.format("Dựa trên %d đoạn tài liệu từ %s, nội dung liên quan tới '%s' là:\n\n%s", 
                    chunks.size(), String.join(", ", sources), question, chunks.get(0).getContent());
        } else {
            answerText = "Hiện tại tài liệu bài giảng chưa có nội dung nào khớp với câu hỏi của bạn.";
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
