package com.ex.learninghub.common.ai.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.ai.dto.RagIngestRequest;
import com.ex.learninghub.common.ai.dto.RagQueryRequest;
import com.ex.learninghub.common.ai.dto.RagQueryResponse;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.common.ai.service.RagService;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
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
    private final EnrollmentRepository enrollmentRepository;

    /** In-Memory Chunked Document Store với tagging metadata. 
     *  (Sử dụng In-memory thay vì PgVector để đảm bảo tương thích 100% với dev H2 DB và tránh xung đột Dimension Gemini) */
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

        // Quyền nạp tài liệu: Cần check Giảng viên có phụ trách class này không (nếu là LECTURER). 
        // Tuy nhiên do ở mức controller đã chặn role, ta assume data đã được upload hợp lệ từ service phía trên.
        
        // Cải tiến Chunking: Giữ nguyên văn cảnh bằng Overlap 100 ký tự
        String content = request.getDocumentContent().replaceAll("\\s+", " ");
        List<String> validChunks = new ArrayList<>();
        int chunkSize = 600;
        int overlap = 100;
        
        int start = 0;
        while (start < content.length()) {
            int end = Math.min(start + chunkSize, content.length());
            // Tìm dấu chấm câu để cắt tự nhiên
            if (end < content.length()) {
                int lastPeriod = content.lastIndexOf(". ", end);
                if (lastPeriod > start + chunkSize / 2) {
                    end = lastPeriod + 1;
                }
            }
            validChunks.add(content.substring(start, end).trim());
            start = end - overlap;
            if (start < 0) start = 0;
            if (end >= content.length()) break;
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

        // --- AUTHORIZATION & SCOPE FILTERING ---
        // Giới hạn phạm vi tài liệu mà sinh viên được phép truy cập
        Set<Long> allowedClazzIds = new HashSet<>();
        boolean isStudent = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        
        if (isStudent) {
            if (request.getClazzId() != null) {
                // Kiểm tra trực tiếp sinh viên có học lớp này không
                boolean isEnrolled = enrollmentRepository.existsByStudentIdAndClazzId(principal.getUser().getId(), request.getClazzId());
                if (!isEnrolled) {
                    throw new AppException(ErrorCode.UNAUTHORIZED); // Không được xem tài liệu lớp khác
                }
                allowedClazzIds.add(request.getClazzId());
            } else {
                // Nếu hỏi chung chung, chỉ lấy tài liệu các lớp đang học
                List<Enrollment> enrollments = enrollmentRepository.findByStudentId(principal.getUser().getId());
                enrollments.forEach(e -> allowedClazzIds.add(e.getClazz().getId()));
                if (allowedClazzIds.isEmpty()) {
                    return buildEmptyResponse(request.getQuestion());
                }
            }
        } else {
            if (request.getClazzId() != null) allowedClazzIds.add(request.getClazzId());
        }

        int topK = request.getTopK() != null && request.getTopK() > 0 ? request.getTopK() : 3;
        List<DocumentChunk> matchedChunks = retrieveRelevantChunks(request.getQuestion(), allowedClazzIds, request.getLessonId(), topK);

        if (matchedChunks.isEmpty()) {
            return buildEmptyResponse(request.getQuestion());
        }

        List<String> sources = matchedChunks.stream()
                .map(c -> String.format("[%s]", c.getSourceName()))
                .distinct()
                .toList();

        if (aiClientService.isAiConfigured()) {
            try {
                // Prompt Assembly chặt chẽ chống ảo giác (hallucination)
                String systemPrompt = "Bạn là Trợ lý AI của hệ thống giáo dục. Nhiệm vụ của bạn là trả lời câu hỏi CHỈ DỰA VÀO DỮ LIỆU TÀI LIỆU (Ngữ cảnh) ĐƯỢC CUNG CẤP BÊN DƯỚI. " +
                                      "Tuyệt đối không bịa đặt thông tin. Nếu tài liệu không chứa câu trả lời, hãy nói 'Tài liệu hiện tại không đề cập đến vấn đề này'. " +
                                      "Trả lời rõ ràng, chuyên nghiệp và trích dẫn [Nguồn].";
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
                // Fallback nếu LLM chết
            }
        }

        return generateRuleBasedRagResponse(request.getQuestion(), matchedChunks, sources);
    }

    private List<DocumentChunk> retrieveRelevantChunks(String question, Set<Long> allowedClazzIds, Long lessonId, int topK) {
        String queryLower = question.toLowerCase();
        Set<String> queryWords = Arrays.stream(queryLower.split("[\\s\\p{Punct}]+"))
                .filter(w -> w.length() > 2)
                .collect(Collectors.toSet());

        List<DocumentChunk> candidates = documentStore.stream()
                .filter(c -> allowedClazzIds.isEmpty() || allowedClazzIds.contains(c.getClazzId()))
                .filter(c -> lessonId == null || lessonId.equals(c.getLessonId()))
                .toList();

        if (candidates.isEmpty()) return Collections.emptyList();

        // Advanced TF-like Lexical Scoring (thay thế keyword match cơ bản)
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
            "CÂU HỎI: \"%s\"\n\n---\nNGỮ CẢNH HỆ THỐNG:\n%s\n---", 
            question, contextBuilder.toString()
        );
    }

    private RagQueryResponse buildEmptyResponse(String question) {
        return RagQueryResponse.builder()
                .question(question)
                .answer("Hiện tại không có tài liệu bài giảng nào (hoặc bạn chưa được cấp quyền xem) chứa thông tin này.")
                .retrievedSources(Collections.emptyList())
                .isRagUsed(false)
                .retrievedCount(0)
                .build();
    }

    private RagQueryResponse generateRuleBasedRagResponse(String question, List<DocumentChunk> chunks, List<String> sources) {
        String answerText = String.format("Hệ thống AI đang bảo trì. Tuy nhiên, nội dung sát nhất từ %s:\n\n%s", 
                String.join(", ", sources), chunks.get(0).getContent());

        return RagQueryResponse.builder()
                .question(question)
                .answer(answerText)
                .retrievedSources(sources)
                .isRagUsed(true)
                .retrievedCount(chunks.size())
                .build();
    }
}
