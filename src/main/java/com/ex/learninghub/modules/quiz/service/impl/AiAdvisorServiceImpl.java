package com.ex.learninghub.modules.quiz.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.quiz.dto.request.AiAdvisorRequest;
import com.ex.learninghub.modules.quiz.dto.response.AiAdvisorResponse;
import com.ex.learninghub.modules.quiz.dto.response.AiAdvisorResponse.StudyPlanStep;
import com.ex.learninghub.modules.quiz.service.AiAdvisorService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAdvisorServiceImpl implements AiAdvisorService {

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final AiClientService aiClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AiAdvisorResponse analyzeCurrentStudent(UserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return generateAnalysis(principal.getUser().getId(), null, principal);
    }

    @Override
    public AiAdvisorResponse analyzeStudentById(Long studentId, UserPrincipal principal) {
        if (studentId == null) {
            throw new AppException(ErrorCode.KEY_INVALID);
        }
        return generateAnalysis(studentId, null, principal);
    }

    @Override
    public AiAdvisorResponse askAdvisor(AiAdvisorRequest request, UserPrincipal principal) {
        Long sid = (request != null && request.getStudentId() != null) 
                ? request.getStudentId() 
                : (principal != null && principal.getUser() != null ? principal.getUser().getId() : null);

        if (sid == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String query = request != null ? request.getCustomQuery() : null;
        return generateAnalysis(sid, query, principal);
    }

    private AiAdvisorResponse generateAnalysis(Long studentId, String customQuery, UserPrincipal principal) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        double totalGradeSum = 0;
        int gradeCount = 0;

        for (Grade g : grades) {
            if (g.getTotalScore() != null) {
                double score = g.getTotalScore().doubleValue();
                totalGradeSum += score;
                gradeCount++;
            }
        }

        double avgGpa = gradeCount > 0 ? (totalGradeSum / gradeCount) : 7.5;
        double gpa4Scale = (avgGpa / 10.0) * 4.0;

        if (aiClientService.isAiConfigured()) {
            try {
                return callAiAdvisorApi(student, avgGpa, gpa4Scale, grades, customQuery);
            } catch (Exception e) {
                log.warn("Gọi AI Advisor API thất bại, chuyển sang chế độ phân tích quy tắc thông minh: {}", e.getMessage());
            }
        }

        return generateRuleBasedAdvisorResponse(student, avgGpa, gpa4Scale, grades, customQuery);
    }

    private AiAdvisorResponse callAiAdvisorApi(User student, double avg10, double avg4, List<Grade> grades, String customQuery) throws Exception {
        String displayName = student.getFullName() != null ? student.getFullName() : student.getEmail();
        String prompt = String.format("""
            Bạn là Cố vấn Học tập AI chuyên nghiệp dành cho sinh viên %s (GPA: %.2f/10, %.2f/4).
            Số môn học đã có điểm: %d.
            Thắc mắc từ sinh viên (nếu có): %s.
            
            Hãy trả về duy nhất một đối tượng JSON hợp lệ (KHÔNG kèm markdown format ```json) theo cấu trúc:
            {
              "studentId": %d,
              "studentName": "%s",
              "gpa": %.2f,
              "academicStatus": "GOOD / EXCELLENT / WARNING",
              "learningStyle": "Mô tả ngắn phong cách học tập",
              "strengths": ["Điểm mạnh 1", "Điểm mạnh 2"],
              "weaknesses": ["Điểm yếu 1", "Điểm yếu 2"],
              "recommendations": ["Khuyến nghị 1", "Khuyến nghị 2"],
              "studyPlan": [
                {
                  "stepOrder": 1,
                  "actionTitle": "Tên bước",
                  "description": "Chi tiết công việc",
                  "timeframe": "Tuần 1-2"
                }
              ],
              "aiAdviceSummary": "Đoạn văn tư vấn tổng quan truyền cảm hứng và định hướng cụ thể."
            }
            """, 
            displayName,
            avg10, avg4, grades.size(),
            customQuery != null ? customQuery : "Không có",
            student.getId(),
            displayName,
            avg10
        );

        String rawText = aiClientService.generateContent(prompt);
        if (rawText != null) {
            String text = rawText.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(text, AiAdvisorResponse.class);
        }
        return generateRuleBasedAdvisorResponse(student, avg10, avg4, grades, customQuery);
    }

    private AiAdvisorResponse generateRuleBasedAdvisorResponse(User student, double avg10, double avg4, List<Grade> grades, String customQuery) {
        String status;
        String learningStyle;
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        List<StudyPlanStep> studyPlan = new ArrayList<>();

        if (avg10 >= 8.5) {
            status = "EXCELLENT";
            learningStyle = "Chủ động & Tư duy Hệ thống (High Academic Performers)";
            strengths.add("Tự giác nghiên cứu tài liệu nâng cao và làm bài tập đúng hạn");
            strengths.add("Tiếp thu nhanh bài giảng thực hành và lý thuyết chuyên sâu");
            weaknesses.add("Cần phân bổ thời gian nghỉ ngơi hợp lý để tránh quá tải");
            recommendations.add("Đăng ký tham gia nghiên cứu khoa học hoặc trợ giảng hỗ trợ giảng viên");
            recommendations.add("Thử sức với các dự án thực tế lớn hoặc luyện thi chứng chỉ quốc tế");
        } else if (avg10 >= 6.5) {
            status = "GOOD";
            learningStyle = "Thực hành - Trực quan (Visual & Interactive Learner)";
            strengths.add("Tham gia lớp đầy đủ và thực hiện tốt các bài tập trắc nghiệm trên hệ thống");
            strengths.add("Khả năng làm việc nhóm tốt khi làm đồ án môn học");
            weaknesses.add("Kỹ năng ôn tập lý thuyết trước các kỳ thi giữa kỳ / cuối kỳ cần cải thiện");
            recommendations.add("Xây dựng sơ đồ tư duy (Mindmap) cho từng chương học phần");
            recommendations.add("Ôn tập lại các bài giảng video trên hệ thống LMS ít nhất 30 phút mỗi ngày");
        } else {
            status = "WARNING";
            learningStyle = "Cần hỗ trợ gia tăng tương tác (Experiential & Step-by-Step Learner)";
            strengths.add("Có tinh thần học hỏi khi được hướng dẫn trực tiếp");
            weaknesses.add("Điểm thi giữa kỳ / bài tập cá nhân còn ở mức thấp");
            weaknesses.add("Có nguy cơ rủi ro học tập nếu không bổ sung lượng kiến thức hổng");
            recommendations.add("Liên hệ Giảng viên chủ nhiệm để được hỗ trợ kèm cặp (Mentorship)");
            recommendations.add("Hoàn thành 100% các bài quiz trắc nghiệm rèn luyện trên LMS");
        }

        studyPlan.add(StudyPlanStep.builder()
                .stepOrder(1)
                .actionTitle("Rà soát & Hệ thống hóa Kiến thức")
                .description("Xem lại toàn bộ slide bài giảng và tóm tắt công thức / khái niệm cốt lõi theo từng môn.")
                .timeframe("Tuần 1 - 2")
                .build());

        studyPlan.add(StudyPlanStep.builder()
                .stepOrder(2)
                .actionTitle("Luyện tập Trắc nghiệm AI & Bài tập Thực hành")
                .description("Sử dụng công cụ AI Quiz trên LMS để làm các bộ đề ôn tập tự động theo chủ đề môn học.")
                .timeframe("Tuần 3 - 4")
                .build());

        studyPlan.add(StudyPlanStep.builder()
                .stepOrder(3)
                .actionTitle("Tương tác Nhóm & Tham vấn Giảng viên")
                .description("Đăng câu hỏi lên Forum học tập của lớp và trao đổi trực tiếp với giảng viên trong giờ thảo luận.")
                .timeframe("Tuần 5 - 6")
                .build());

        studyPlan.add(StudyPlanStep.builder()
                .stepOrder(4)
                .actionTitle("Thi thử & Đánh giá Tiến độ")
                .description("Thực hiện bài kiểm tra giả định cuối kỳ để kiểm tra mức độ sẵn sàng và tối ưu hóa thời gian làm bài.")
                .timeframe("Tuần 7+")
                .build());

        String name = student.getFullName() != null && !student.getFullName().isBlank() 
                ? student.getFullName() 
                : student.getEmail();

        String summary = String.format(
                "Chào %s! Dựa trên phân tích điểm số trung bình (%.2f/10), AI Cố vấn đề xuất bạn duy trì phong cách học tập '%s'. %s",
                name, avg10, learningStyle,
                customQuery != null && !customQuery.isBlank()
                        ? "Đối với câu hỏi của bạn: '" + customQuery + "', AI khuyến nghị bạn áp dụng lộ trình 4 bước bên dưới để đạt điểm tối ưu."
                        : "Hãy làm theo lộ trình 4 bước được thiết kế riêng bên dưới để tối ưu hóa kết quả học tập kỳ này!"
        );

        return AiAdvisorResponse.builder()
                .studentId(student.getId())
                .studentName(name)
                .gpa(Math.round(avg10 * 100.0) / 100.0)
                .academicStatus(status)
                .learningStyle(learningStyle)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .recommendations(recommendations)
                .studyPlan(studyPlan)
                .aiAdviceSummary(summary)
                .build();
    }
}
