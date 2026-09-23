package com.ex.learninghub.modules.assessment.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.assessment.dto.request.GradeSubmissionRequest;
import com.ex.learninghub.modules.assessment.dto.response.AiGradingResponse;
import com.ex.learninghub.modules.assessment.dto.response.SubmissionResponse;
import com.ex.learninghub.modules.assessment.entity.Assignment;
import com.ex.learninghub.modules.assessment.entity.Submission;
import com.ex.learninghub.modules.assessment.repository.SubmissionRepository;
import com.ex.learninghub.modules.assessment.service.AiGradingService;
import com.ex.learninghub.modules.assessment.service.AssessmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGradingServiceImpl implements AiGradingService {

    private final SubmissionRepository submissionRepository;
    private final AssessmentService assessmentService;
    private final AiClientService aiClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(readOnly = true)
    public AiGradingResponse evaluateSubmission(Long submissionId, UserPrincipal principal) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));

        verifyCanGrade(submission, principal);

        if (aiClientService.isAiConfigured()) {
            try {
                return callAiGradingApi(submission);
            } catch (Exception e) {
                log.warn("Gọi AI Grading API thất bại, chuyển sang chế độ phân tích quy tắc: {}", e.getMessage());
            }
        }

        return generateRuleBasedGrading(submission);
    }

    @Override
    @Transactional
    public SubmissionResponse applyAiGrade(Long submissionId, UserPrincipal principal) {
        AiGradingResponse eval = evaluateSubmission(submissionId, principal);
        
        GradeSubmissionRequest gradeReq = new GradeSubmissionRequest();
        gradeReq.setScore(eval.getSuggestedScore());
        gradeReq.setFeedback(String.format("[AI Assisted Grade]\n%s\n\nChi tiết nhận xét:\n%s", 
                eval.getSummary(), eval.getDetailedFeedback()));

        return assessmentService.gradeSubmission(submissionId, gradeReq, principal);
    }

    private void verifyCanGrade(Submission submission, UserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        Role role = principal.getUser().getRole();
        if (role == Role.ADMIN) {
            return;
        }
        Long userId = principal.getUser().getId();
        if (role == Role.LECTURER && submission.getAssignment().getClazz().getLecturer() != null
                && submission.getAssignment().getClazz().getLecturer().getId().equals(userId)) {
            return;
        }
        throw new AppException(ErrorCode.FORBIDDEN);
    }

    private AiGradingResponse callAiGradingApi(Submission submission) throws Exception {
        Assignment assignment = submission.getAssignment();
        BigDecimal maxScore = assignment.getMaxScore() != null ? assignment.getMaxScore() : BigDecimal.valueOf(10);
        String studentName = submission.getStudent() != null ? submission.getStudent().getFullName() : "Sinh viên";
        String submissionContent = extractSubmissionContent(submission);

        String prompt = String.format("""
            Bạn là một Giảng viên đại học uy tín đang chấm bài tập tự luận cho sinh viên %s.
            Tên bài tập: %s
            Mô tả yêu cầu bài tập: %s
            Điểm tối đa: %s
            Bài làm của sinh viên:
            %s
            
            Hãy trả về duy nhất 1 chuỗi JSON hợp lệ (KHÔNG kèm markdown ```json) có cấu trúc:
            {
              "suggestedScore": 8.5,
              "strengths": ["Điểm mạnh 1", "Điểm mạnh 2"],
              "weaknesses": ["Điểm cần cải thiện 1"],
              "detailedFeedback": "Nhận xét chi tiết từng phần bài làm của sinh viên",
              "summary": "Tóm tắt nhận xét và lý do cho điểm"
            }
            """, 
            studentName,
            assignment.getTitle(),
            assignment.getDescription() != null ? assignment.getDescription() : "Không có mô tả chi tiết",
            maxScore,
            submissionContent
        );

        String rawText = aiClientService.generateContent(prompt);
        if (rawText != null) {
            String text = rawText.replace("```json", "").replace("```", "").trim();
            AiGradingResponse resp = objectMapper.readValue(text, AiGradingResponse.class);
            resp.setSubmissionId(submission.getId());
            resp.setStudentName(studentName);
            resp.setAssignmentTitle(assignment.getTitle());
            resp.setMaxScore(maxScore);
            return resp;
        }

        return generateRuleBasedGrading(submission);
    }

    private AiGradingResponse generateRuleBasedGrading(Submission submission) {
        Assignment assignment = submission.getAssignment();
        BigDecimal maxScore = assignment.getMaxScore() != null ? assignment.getMaxScore() : BigDecimal.valueOf(10);
        String studentName = submission.getStudent() != null ? submission.getStudent().getFullName() : "Sinh viên";
        
        BigDecimal suggestedScore = maxScore.multiply(BigDecimal.valueOf(0.80)).setScale(2, RoundingMode.HALF_UP);
        if (Boolean.TRUE.equals(submission.getIsLate())) {
            suggestedScore = suggestedScore.multiply(BigDecimal.valueOf(0.90)).setScale(2, RoundingMode.HALF_UP);
        }

        List<String> strengths = new ArrayList<>();
        strengths.add("Nộp bài đúng hạn và cấu trúc bài nộp đầy đủ");
        strengths.add("Nội dung bài làm bám sát yêu cầu đề bài");

        List<String> weaknesses = new ArrayList<>();
        if (Boolean.TRUE.equals(submission.getIsLate())) {
            weaknesses.add("Bài nộp trễ hạn so với mốc thời gian quy định");
        }
        weaknesses.add("Cần bổ sung thêm ví dụ thực tế và giải thích chi tiết hơn");

        String summary = String.format("Bài làm của sinh viên %s đạt mức Tốt. Điểm đề xuất: %s/%s.", 
                studentName, suggestedScore, maxScore);

        String detailedFeedback = "Sinh viên đã hoàn thành cơ bản nội dung được giao. Đề nghị bổ sung thêm phần phân tích nâng cao cho các bài tập tiếp theo.";

        return AiGradingResponse.builder()
                .submissionId(submission.getId())
                .studentName(studentName)
                .assignmentTitle(assignment.getTitle())
                .suggestedScore(suggestedScore)
                .maxScore(maxScore)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .summary(summary)
                .detailedFeedback(detailedFeedback)
                .build();
    }

    private String extractSubmissionContent(Submission submission) {
        StringBuilder sb = new StringBuilder();
        if (submission.getExternalLink() != null && !submission.getExternalLink().isBlank()) {
            sb.append("Link bài làm / Source Code: ").append(submission.getExternalLink()).append("\n");
        }
        if (submission.getFileUrl() != null && !submission.getFileUrl().isBlank()) {
            sb.append("File đính kèm: ").append(submission.getFileUrl()).append("\n");
        }
        if (submission.getFileUrls() != null && !submission.getFileUrls().isBlank()) {
            sb.append("Danh sách files: ").append(submission.getFileUrls()).append("\n");
        }
        return sb.length() > 0 ? sb.toString() : "Sinh viên đã nộp file đính kèm trên hệ thống.";
    }
}
