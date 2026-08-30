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
    private Long curriculumId;
    private java.util.List<String> permissions;

    /** Factory method to create UserResponse from User entity */
    public static UserResponse from(User user) {
        java.util.List<String> perms = user.getAdminPermissions() != null ?
                user.getAdminPermissions().stream()
                        .map(p -> p.getCode().name())
                        .collect(java.util.stream.Collectors.toList()) : java.util.List.of();

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .studentCode(user.getStudentCode())
                .lecturerCode(user.getLecturerCode())
                .faculty(user.getFaculty())
                .major(user.getMajor())
                .curriculumId(user.getCurriculum() != null ? user.getCurriculum().getId() : null)
                .permissions(perms)
                .build();
    }
}