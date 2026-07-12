package com.ex.learninghub.modules.user.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(request.getRole())
                .studentCode(request.getStudentCode())
                .lecturerCode(request.getLecturerCode())
                .dateOfBirth(request.getDateOfBirth())
                .faculty(request.getFaculty())
                .major(request.getMajor())
                .password(passwordEncoder.encode("Password@123")) // Default password
                .isFirstLogin(true)
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public List<User> importStudentsFromExcel(MultipartFile file) {
        return importUsers(file, Role.STUDENT);
    }

    @Override
    @Transactional
    public List<User> importLecturersFromExcel(MultipartFile file) {
        return importUsers(file, Role.LECTURER);
    }

    private List<User> importUsers(MultipartFile file, Role role) {
        List<User> users = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String code = getCellValue(row.getCell(1));
                String fullName = getCellValue(row.getCell(2));
                String dobStr = getCellValue(row.getCell(3));

                if (code.isEmpty() || fullName.isEmpty()) continue;

                String email = code.toLowerCase() + (role == Role.STUDENT ? "@student.edu.vn" : "@lecturer.edu.vn");

                // Skip if exists
                if (userRepository.existsByEmail(email) || 
                    (role == Role.STUDENT && userRepository.existsByStudentCode(code)) ||
                    (role == Role.LECTURER && userRepository.existsByLecturerCode(code))) {
                    continue;
                }

                User user = User.builder()
                        .fullName(fullName)
                        .email(email)
                        .role(role)
                        .studentCode(role == Role.STUDENT ? code : null)
                        .lecturerCode(role == Role.LECTURER ? code : null)
                        .password(passwordEncoder.encode("Password@123"))
                        .isFirstLogin(true)
                        .build();

                try {
                    if (!dobStr.isEmpty()) {
                        user.setDateOfBirth(LocalDate.parse(dobStr, dateFormatter));
                    }
                } catch (Exception ignored) {
                    // Ignore parsing error, keep null
                }

                users.add(userRepository.save(user));
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS); // Change this to a better error later
        }

        return users;
    }

    private String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            default: return "";
        }
    }
}
