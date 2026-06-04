package com.ex.learninghub.modules.user.service;

import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.dto.request.UserUpdateRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    void deleteUser(Long id);
}
