package com.ex.learninghub.modules.user.dto.response;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String studentCode;
    private String lecturerCode;
    private LocalDate dateOfBirth;
    private String faculty;
    private String major;
    private Long adminClassId;
    private String adminClassName;
    private Boolean isFirstLogin;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        // Với sinh viên: faculty lấy từ adminClass nếu không có faculty riêng
        String faculty = user.getFaculty();
        if (faculty == null && user.getAdminClass() != null) {
            faculty = user.getAdminClass().getFaculty();
        }

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .studentCode(user.getStudentCode())
                .lecturerCode(user.getLecturerCode())
                .dateOfBirth(user.getDateOfBirth())
                .faculty(faculty)
                .major(user.getMajor())
                .adminClassId(user.getAdminClass() != null ? user.getAdminClass().getId() : null)
                .adminClassName(user.getAdminClass() != null ? user.getAdminClass().getClassName() : null)
                .isFirstLogin(user.getIsFirstLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
