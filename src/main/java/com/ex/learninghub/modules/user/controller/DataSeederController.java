package com.ex.learninghub.modules.user.controller;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/seeder")
@RequiredArgsConstructor
@Tag(name = "Data Seeder", description = "Endpoints for seeding and cleaning test data")
public class DataSeederController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    private static final String SEED_EMAIL_SUFFIX = "@seed.lms";

    @PostMapping("/students")
    @Operation(summary = "Tạo tự động sinh viên", description = "Tạo hàng loạt sinh viên ảo phục vụ test. Các sinh viên này sẽ có đuôi email là @seed.lms để dễ dàng phân biệt và xoá.")
    @Transactional
    public ResponseEntity<?> seedStudents(@RequestParam(defaultValue = "50") int count) {
        List<User> students = new ArrayList<>();
        String defaultPassword = passwordEncoder.encode("123456");

        long startId = System.currentTimeMillis() % 100000;

        for (int i = 0; i < count; i++) {
            long currentNum = startId + i;
            User student = User.builder()
                    .fullName("Seed Student " + currentNum)
                    .email("sv" + currentNum + SEED_EMAIL_SUFFIX)
                    .password(defaultPassword)
                    .role(Role.STUDENT)
                    .studentCode("SV" + currentNum)
                    .faculty("Công nghệ thông tin")
                    .major("Kỹ thuật phần mềm")
                    .isFirstLogin(false)
                    .build();
            students.add(student);
        }

        userRepository.saveAll(students);
        return ResponseEntity.ok(Map.of(
                "message", "Đã tạo thành công " + count + " sinh viên ảo.",
                "sample_email", students.get(0).getEmail(),
                "password", "123456"
        ));
    }

    @DeleteMapping("/students")
    @Operation(summary = "Dọn dẹp sinh viên ảo", description = "Xoá TẤT CẢ sinh viên ảo (có đuôi @seed.lms) cùng với toàn bộ dữ liệu liên quan (điểm, bài nộp, v.v) ra khỏi hệ thống.")
    @Transactional
    public ResponseEntity<?> cleanSeededStudents() {
        String suffix = "%" + SEED_EMAIL_SUFFIX;

        // Xoá dữ liệu liên quan trước để tránh lỗi Foreign Key
        String[] tables = {
                "enrollments", "submissions", "quiz_attempts", "grades",
                "attendance", "forum_comments", "forum_posts", "notifications",
                "tuition_invoices", "refresh_tokens", "password_reset_tokens"
        };

        for (String table : tables) {
            String col = (table.equals("forum_comments") || table.equals("forum_posts")) ? "author_id"
                    : (table.equals("notifications") ? "recipient_id"
                    : (table.equals("refresh_tokens") || table.equals("password_reset_tokens") ? "user_id" : "student_id"));
            
            try {
                entityManager.createNativeQuery(
                        "DELETE FROM " + table + " WHERE " + col + " IN (SELECT id FROM users WHERE email LIKE :suffix)"
                ).setParameter("suffix", suffix).executeUpdate();
            } catch (Exception e) {
                // Ignore if table doesn't exist or column is slightly different in some branch
            }
        }
        
        // Clean up video related stuff specifically for this project
        try {
            entityManager.createNativeQuery("DELETE FROM student_video_notes WHERE student_id IN (SELECT id FROM users WHERE email LIKE :suffix)").setParameter("suffix", suffix).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM video_progress WHERE student_id IN (SELECT id FROM users WHERE email LIKE :suffix)").setParameter("suffix", suffix).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM user_ai_preferences WHERE user_id IN (SELECT id FROM users WHERE email LIKE :suffix)").setParameter("suffix", suffix).executeUpdate();
        } catch (Exception e) {}

        // Cuối cùng xoá user
        int deletedUsers = entityManager.createNativeQuery(
                "DELETE FROM users WHERE email LIKE :suffix"
        ).setParameter("suffix", suffix).executeUpdate();

        return ResponseEntity.ok(Map.of(
                "message", "Đã dọn dẹp sạch sẽ dữ liệu rác.",
                "deleted_users", deletedUsers
        ));
    }
}
