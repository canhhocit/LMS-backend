package com.ex.learninghub.modules.quiz.controller;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.quiz.dto.request.AiAdvisorRequest;
import com.ex.learninghub.modules.quiz.dto.response.AiAdvisorResponse;
import com.ex.learninghub.modules.quiz.service.AiAdvisorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai/advisor")
@RequiredArgsConstructor
@Tag(name = "AI Academic Advisor", description = "Các API Cố vấn Học tập AI - Phân tích phong cách học tập & đề xuất lộ trình cải thiện điểm số")
public class AiAdvisorController {

    private final AiAdvisorService aiAdvisorService;
    private final AiClientService aiClientService;

    @GetMapping("/my-analysis")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(
            summary = "Phân tích phong cách học tập & đề xuất cho sinh viên hiện tại",
            description = "Tự động tổng hợp điểm số, tính toán nhãn trạng thái và sinh lộ trình cải thiện học tập cá nhân hóa."
    )
    public ApiResponse<AiAdvisorResponse> getMyAnalysis(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(aiAdvisorService.analyzeCurrentStudent(principal));
    }

    @PostMapping("/analyze-student")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(
            summary = "Phân tích cố vấn học tập cho sinh viên chỉ định (Dành cho Giảng viên/Admin)",
            description = "Giảng viên chọn sinh viên bất kỳ để hệ thống AI cố vấn đưa ra báo cáo phân tích và lộ trình đề xuất."
    )
    public ApiResponse<AiAdvisorResponse> analyzeStudent(
            @RequestParam Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(aiAdvisorService.analyzeStudentById(studentId, principal));
    }

    @PostMapping("/ask")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'ADMIN')")
    @Operation(
            summary = "Đặt câu hỏi tham vấn trực tiếp với AI Advisor",
            description = "Sinh viên/Giảng viên nhập thắc mắc cụ thể về môn học để nhận định hướng giải đáp từ Cố vấn AI."
    )
    public ApiResponse<AiAdvisorResponse> askAdvisor(
            @RequestBody AiAdvisorRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(aiAdvisorService.askAdvisor(request, principal));
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'ADMIN')")
    @Operation(
            summary = "Trò chuyện trực tiếp với Hikari AI Mascot",
            description = "Gửi tin nhắn bất kỳ tới Hikari AI Companion để nhận câu trả lời tư vấn học tập hoặc thông tin hệ thống."
    )
    public ApiResponse<Map<String, String>> chatWithAi(
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal UserPrincipal principal) {
        String prompt = requestBody != null ? requestBody.get("prompt") : "";
        if (prompt == null || prompt.isBlank()) {
            prompt = "Xin chào Hikari AI!";
        }

        String reply;
        if (aiClientService.isAiConfigured()) {
            try {
                String userName = principal != null && principal.getUser() != null ? principal.getUser().getFullName() : "Bạn học";
                String systemPrompt = String.format("""
                        TÊN VÀ DANH TÍNH CỦA BẠN: Bạn BẮT BUỘC KHẲNG ĐỊNH TÊN LÀ "Hikari AI" (Trợ lý AI Mascot học tập 24/7 của hệ thống LearningHub LMS).
                        QUY TẮC DANH TÍNH NGHIÊM NGẶT:
                        - Tên duy nhất của bạn là Hikari AI (hoặc Hikari).
                        - Tuyệt đối KHÔNG ĐƯỢC tự xưng mình là Qwen, Qwen3.8, ChatGPT, OpenAI, Llama, Gemini hay bất kỳ tên mô hình LLM nền tảng nào khác.
                        - Nếu người dùng hỏi bạn tên gì hay bạn là ai ("bạn là ai", "what's your name", "who are you"), khẳng định bạn là Hikari AI.
                        NGÔN NGỮ VÀ PHONG CÁCH PHẢN HỒI:
                        - Trả lời theo ĐÚNG ngôn ngữ mà người dùng yêu cầu hoặc đang sử dụng. NẾU người dùng hỏi bằng Tiếng Anh hoặc yêu cầu trả lời Tiếng Anh, bạn BẮT BUỘC phản hồi hoàn toàn bằng Tiếng Anh (English). NẾU bằng Tiếng Việt, phản hồi bằng Tiếng Việt.
                        - Người dùng hiện tại tên là: %s. Xưng hô lịch sự, ngắn gọn (dưới 150 từ), súc tích và hỗ trợ học tập tốt nhất.
                        """, userName);
                reply = aiClientService.generateContent(systemPrompt, prompt);
            } catch (Exception e) {
                log.warn("Gọi AI Chat thất bại: {}", e.getMessage());
                reply = "🤖 Hikari AI: Trả lời yêu cầu \"" + prompt + "\": Dữ liệu học phần & lịch học của bạn đã được cập nhật mới nhất!";
            }
        } else {
            reply = "🤖 Hikari AI: Trả lời yêu cầu \"" + prompt + "\": Dữ liệu học phần & lịch học của bạn đã được cập nhật mới nhất! (Vui lòng điền AI_API_KEY trong file .env nếu muốn trò chuyện với AI thật)";
        }

        return ApiResponse.success(Map.of("reply", reply));
    }
}
