package com.ex.learninghub.modules.user.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.user.dto.request.UpdateProfileRequest;
import com.ex.learninghub.modules.user.dto.request.UserCreateRequest;
import com.ex.learninghub.modules.user.dto.response.UserResponse;
import com.ex.learninghub.modules.user.entity.AdministrativeClass;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.AdministrativeClassRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${app.default-password:Password@123}")
    private String defaultPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdministrativeClassRepository adminClassRepository;
    private final com.ex.learninghub.modules.curriculum.repository.CurriculumRepository curriculumRepository;

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
                .password(passwordEncoder.encode(defaultPassword))
                .isFirstLogin(true)
                .build();

        if (request.getAdminClassId() != null) {
            AdministrativeClass adminClass = adminClassRepository.findById(request.getAdminClassId())
                    .orElseThrow(() -> new AppException(ErrorCode.ADMIN_CLASS_NOT_FOUND));
            user.setAdminClass(adminClass);
        }

        if (request.getCurriculumId() != null) {
            var curriculum = curriculumRepository.findById(request.getCurriculumId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
            user.setCurriculum(curriculum);
        }

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

    @Override
    public Page<UserResponse> getStudents(String keyword, Pageable pageable) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        Page<User> users = kw == null
            ? userRepository.findByRole(Role.STUDENT, pageable)
            : userRepository.findByRoleAndKeyword(Role.STUDENT, kw, pageable);
        return users
                .map(UserResponse::from);
    }

    @Override
    public Page<UserResponse> getLecturers(String keyword, Pageable pageable) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        Page<User> users = kw == null
            ? userRepository.findByRole(Role.LECTURER, pageable)
            : userRepository.findByRoleAndKeyword(Role.LECTURER, kw, pageable);
        return users
                .map(UserResponse::from);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Override
    public byte[] exportStudentsToExcel(String keyword) throws IOException {
        return exportUsersToExcel(keyword, Role.STUDENT);
    }

    @Override
    public byte[] exportLecturersToExcel(String keyword) throws IOException {
        return exportUsersToExcel(keyword, Role.LECTURER);
    }

    private byte[] exportUsersToExcel(String keyword, Role role) throws IOException {
        // Template: STT | Lớp | MSV | Họ đệm | Tên | Ngày sinh
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        Pageable allPages = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("fullName"));
        List<User> users = (kw == null
            ? userRepository.findByRole(role, allPages)
            : userRepository.findByRoleAndKeyword(role, kw, allPages)).getContent();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Users");

            // Header row
            Row header = sheet.createRow(0);
            String[] headers = {"STT", "Lớp", role == Role.STUDENT ? "MSV" : "Mã GV", "Họ đệm", "Tên", "Ngày sinh"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            // Data rows
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                Row row = sheet.createRow(i + 1);

                // Split fullName into lastName (họ đệm) and firstName (tên)
                String[] nameParts = splitName(user.getFullName());

                row.createCell(0).setCellValue(i + 1);                            // STT
                row.createCell(1).setCellValue(nvl(user.getFaculty()));            // Lớp
                row.createCell(2).setCellValue(nvl(role == Role.STUDENT          // MSV / mã GV
                        ? user.getStudentCode() : user.getLecturerCode()));
                row.createCell(3).setCellValue(nameParts[0]);                      // Họ đệm
                row.createCell(4).setCellValue(nameParts[1]);                      // Tên
                row.createCell(5).setCellValue(user.getDateOfBirth() != null      // Ngày sinh
                        ? user.getDateOfBirth().format(dateFormatter) : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /** Tách "Nguyễn Văn A" → ["Nguyễn Văn", "A"] */
    private String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) return new String[]{"", ""};
        int lastSpace = fullName.lastIndexOf(' ');
        if (lastSpace < 0) return new String[]{"", fullName.trim()};
        return new String[]{fullName.substring(0, lastSpace).trim(), fullName.substring(lastSpace + 1).trim()};
    }

    private String nvl(String value) {
        return value != null ? value : "";
    }

    private List<User> importUsers(MultipartFile file, Role role) {
        List<User> users = new ArrayList<>();
        // Template: STT | Lớp | MSV | Họ đệm | Tên | Ngày sinh
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String className  = getCellValue(row.getCell(1)); // Lớp
                String code       = getCellValue(row.getCell(2)); // MSV / mã GV
                String lastName   = getCellValue(row.getCell(3)); // Họ đệm
                String firstName  = getCellValue(row.getCell(4)); // Tên
                String dobStr     = getCellValue(row.getCell(5)); // Ngày sinh

                if (code.isEmpty() || firstName.isEmpty()) continue;

                // Skip nếu code trông như header (chứa chữ cái không phải mã SV/GV)
                if (!code.matches(".*\\d.*")) continue;

                String fullName = (lastName + " " + firstName).trim();
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
                        .password(passwordEncoder.encode(defaultPassword))
                        .isFirstLogin(true)
                        .build();

                // Sinh viên: gán lớp hành chính (tạo mới nếu chưa có)
                if (role == Role.STUDENT && !className.isEmpty()) {
                    AdministrativeClass adminClass = adminClassRepository.findByClassName(className)
                            .orElseGet(() -> adminClassRepository.save(
                                    AdministrativeClass.builder().className(className).build()));
                    user.setAdminClass(adminClass);
                }

                // Giảng viên: gán faculty trực tiếp
                if (role == Role.LECTURER && !className.isEmpty()) {
                    user.setFaculty(className);
                }

                try {
                    if (!dobStr.isEmpty()) {
                        user.setDateOfBirth(LocalDate.parse(dobStr, dateFormatter));
                    }
                } catch (Exception ignored) {
                    // Ignore parsing error, keep null
                }

                users.add(userRepository.save(user));
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.EXCEL_PARSE_ERROR);
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

    // ---- Profile ----

    @Override
    public UserResponse getProfile(UserPrincipal userPrincipal) {
        return UserResponse.from(userPrincipal.getUser());
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UserPrincipal userPrincipal, UpdateProfileRequest request) {
        User user = userRepository.findById(userPrincipal.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setFullName(request.getFullName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setFaculty(request.getFaculty());
        user.setMajor(request.getMajor());
        return UserResponse.from(userRepository.save(user));
    }

    // ---- Admin operations ----

    @Override
    @Transactional
    public void resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setIsFirstLogin(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserCreateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setFullName(request.getFullName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setFaculty(request.getFaculty());
        user.setMajor(request.getMajor());
        user.setStudentCode(request.getStudentCode());
        user.setLecturerCode(request.getLecturerCode());
        if (request.getCurriculumId() != null) {
            var curriculum = curriculumRepository.findById(request.getCurriculumId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
            user.setCurriculum(curriculum);
        } else {
            user.setCurriculum(null);
        }
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(com.ex.learninghub.common.enums.UserStatus.valueOf(status.toUpperCase()));
        userRepository.save(user);
    }
}
