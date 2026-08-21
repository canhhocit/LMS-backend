package com.ex.learninghub.modules.user.dto.response;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String studentCode;
    private String lecturerCode;
    private String faculty;
    private String major;

    /** Factory method to create UserResponse from User entity */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .studentCode(user.getStudentCode())
                .lecturerCode(user.getLecturerCode())
                .faculty(user.getFaculty())
                .major(user.getMajor())
                .build();
    }
}