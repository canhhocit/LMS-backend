package com.ex.learninghub.modules.user.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.user.dto.request.UpdateProfileRequest;
import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User createUser(UserCreateRequest request);
    List<User> importStudentsFromExcel(MultipartFile file);
    List<User> importLecturersFromExcel(MultipartFile file);

    Page<UserResponse> getStudents(String keyword, Pageable pageable);
    Page<UserResponse> getLecturers(String keyword, Pageable pageable);
    UserResponse getUserById(Long id);

    byte[] exportStudentsToExcel(String keyword) throws IOException;
    byte[] exportLecturersToExcel(String keyword) throws IOException;

    // Profile
    UserResponse getProfile(UserPrincipal userPrincipal);
    UserResponse updateProfile(UserPrincipal userPrincipal, UpdateProfileRequest request);

    // Admin operations
    void resetPassword(Long userId);
    UserResponse updateUser(Long id, UserCreateRequest request);
    void deleteUser(Long id);
    void updateUserStatus(Long id, String status);
    List<UserResponse> getAdmins();
}
