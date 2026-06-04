package com.ex.learninghub.modules.user.mapper;

import org.springframework.stereotype.Component;

import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.entity.User;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toUser(UserCreateRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .avatarUrl(request.getAvatarUrl())
                .build();
    }
}
