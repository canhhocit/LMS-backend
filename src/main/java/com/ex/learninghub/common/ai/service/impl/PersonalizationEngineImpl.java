package com.ex.learninghub.common.ai.service.impl;

import com.ex.learninghub.common.ai.entity.AiPromptTemplate;
import com.ex.learninghub.common.ai.repository.AiPromptTemplateRepository;
import com.ex.learninghub.common.ai.service.PersonalizationEngine;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.common.ai.entity.UserAiPreference;
import com.ex.learninghub.common.ai.repository.UserAiPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalizationEngineImpl implements PersonalizationEngine {

    private final AiPromptTemplateRepository templateRepository;
    private final GradeRepository gradeRepository;
    private final UserAiPreferenceRepository userAiPreferenceRepository;

    @Override
    public String buildPersonalizedSystemPrompt(User student, String templateCode, String lecturerTone) {
        String baseSystemPrompt = getBaseSystemPrompt(templateCode);

        if (student == null) {
            return baseSystemPrompt;
        }

        double studentGpa = calculateStudentGpa(student.getId());
        String personaInstructions = determineStudentPersonaInstructions(student, studentGpa);
        String toneInstruction = lecturerTone != null && !lecturerTone.isBlank() 
                ? "Giọng văn cá nhân hóa giảng viên cài đặt: " + lecturerTone 
                : "";
        String userPreferenceInstructions = buildUserPreferenceInstructions(student.getId());

        return String.format(java.util.Locale.US, """
            %s
            
            --- THÔNG TIN CÁ NHÂN HÓA SINH VIÊN ---
            - Tên sinh viên: %s
            - Mã sinh viên / Chuyên ngành: %s / %s
            - Điểm trung bình tích lũy (GPA): %.2f/10
            - Chỉ dẫn phong cách ứng xử của AI dành riêng cho sinh viên này:
              %s
            
            %s
            %s
            """,
            baseSystemPrompt,
            student.getFullName() != null ? student.getFullName() : student.getEmail(),
            student.getStudentCode() != null ? student.getStudentCode() : "N/A",
            student.getMajor() != null ? student.getMajor() : "Đại học",
            studentGpa,
            personaInstructions,
            toneInstruction,
            userPreferenceInstructions
        );
    }

    private String buildUserPreferenceInstructions(Long userId) {
        if (userId == null || userAiPreferenceRepository == null) return "";

        Optional<UserAiPreference> prefOpt = userAiPreferenceRepository.findByUserId(userId);
        if (prefOpt.isEmpty()) return "";

        UserAiPreference pref = prefOpt.get();
        StringBuilder sb = new StringBuilder("--- THIẾT LẬP CÁ NHÂN HÓA USER PROMPT CỦA NGƯỜI DÙNG ---\n");

        if (pref.getPreferredCallName() != null && !pref.getPreferredCallName().isBlank()) {
            sb.append("- Tên / Cách xưng hô mong muốn: ").append(pref.getPreferredCallName()).append("\n");
        }
        if (pref.getAiTone() != null && !pref.getAiTone().isBlank()) {
            sb.append("- Giọng điệu AI yêu thích: ").append(pref.getAiTone());
            if (pref.getCustomToneDescription() != null && !pref.getCustomToneDescription().isBlank()) {
                sb.append(" (").append(pref.getCustomToneDescription()).append(")");
            }
            sb.append("\n");
        }
        if (pref.getResponseLength() != null && !pref.getResponseLength().isBlank()) {
            sb.append("- Độ chi tiết & độ dài câu trả lời: ").append(pref.getResponseLength()).append("\n");
        }
        if (pref.getCustomContext() != null && !pref.getCustomContext().isBlank()) {
            sb.append("- Ngữ cảnh cá nhân bổ sung: ").append(pref.getCustomContext()).append("\n");
        }

        return sb.toString();
    }

    private String getBaseSystemPrompt(String templateCode) {
        if (templateCode != null && !templateCode.isBlank()) {
            return templateRepository.findByCode(templateCode)
                    .map(AiPromptTemplate::getSystemPrompt)
                    .orElseGet(this::getDefaultSystemPrompt);
        }
        return getDefaultSystemPrompt();
    }

    private String getDefaultSystemPrompt() {
        return templateRepository.findByIsDefaultTrue()
                .map(AiPromptTemplate::getSystemPrompt)
                .orElse("Bạn là Trợ lý AI Cố vấn Học tập chuyên nghiệp và tận tâm dành cho sinh viên.");
    }

    private double calculateStudentGpa(Long studentId) {
        if (studentId == null) return 7.5;
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        if (grades.isEmpty()) return 7.5;

        double sum = 0;
        int count = 0;
        for (Grade g : grades) {
            if (g.getTotalScore() != null) {
                sum += g.getTotalScore().doubleValue();
                count++;
            }
        }
        return count > 0 ? (sum / count) : 7.5;
    }

    private String determineStudentPersonaInstructions(User student, double gpa) {
        if (gpa < 6.0) {
            return "Sinh viên cần hỗ trợ củng cố nền tảng. Hãy kiên nhẫn, sử dụng câu từ động viên tích cực, chia nhỏ câu trả lời thành từng bước dễ hiểu (Step-by-Step).";
        } else if (gpa >= 8.5) {
            return "Sinh viên đạt trình độ Xuất sắc. Hãy trao đổi với giọng văn chuyên sâu, đặt các câu hỏi gợi mở thách thức tư duy và mở rộng ứng dụng thực tế.";
        } else {
            return "Sinh viên có sức học Khá/Tốt. Hãy đưa ra các khuyến nghị rõ ràng, kết hợp sơ đồ tư duy hoặc ví dụ trực quan.";
        }
    }
}
