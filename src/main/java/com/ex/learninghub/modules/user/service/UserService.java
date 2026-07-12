package com.ex.learninghub.modules.user.service;

import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserService {
    User createUser(UserCreateRequest request);
    List<User> importStudentsFromExcel(MultipartFile file);
    List<User> importLecturersFromExcel(MultipartFile file);
}
