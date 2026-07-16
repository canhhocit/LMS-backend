package com.ex.learninghub.modules.grading.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.grading.dto.request.AttendanceRecordItem;
import com.ex.learninghub.modules.grading.dto.request.AttendanceRequest;
import com.ex.learninghub.modules.grading.dto.request.GradeRequest;
import com.ex.learninghub.modules.grading.dto.response.AttendanceResponse;
import com.ex.learninghub.modules.grading.dto.response.GradeResponse;
import com.ex.learninghub.modules.grading.entity.Attendance;
import com.ex.learninghub.modules.grading.entity.Grade;
import com.ex.learninghub.modules.grading.repository.AttendanceRepository;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import com.ex.learninghub.modules.grading.service.GradingService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradingServiceImpl implements GradingService {

    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClazzRepository clazzRepository;
    private final UserRepository userRepository;

    private void verifyLecturerOwnsClazz(Clazz clazz, UserPrincipal userPrincipal) {
        if (clazz.getLecturer() == null ||
                !clazz.getLecturer().getId().equals(userPrincipal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public GradeResponse upsertGrade(Long classId, GradeRequest request, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Grade grade = gradeRepository.findByClazzIdAndStudentId(classId, request.getStudentId())
                .orElse(Grade.builder().clazz(clazz).student(student).build());

        grade.setMidtermScore(request.getMidtermScore());
        grade.setFinalScore(request.getFinalScore());

        if (request.getMidtermScore() != null && request.getFinalScore() != null) {
            BigDecimal total = request.getMidtermScore()
                    .multiply(new BigDecimal("0.4"))
                    .add(request.getFinalScore().multiply(new BigDecimal("0.6")))
                    .setScale(2, RoundingMode.HALF_UP);
            grade.setTotalScore(total);
        } else {
            grade.setTotalScore(null);
        }

        return GradeResponse.from(gradeRepository.save(grade));
    }

    @Override
    public List<GradeResponse> getGradesByClass(Long classId, UserPrincipal userPrincipal) {
        return gradeRepository.findByClazzId(classId).stream()
                .map(GradeResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeResponse> getMyGrades(UserPrincipal userPrincipal) {
        return gradeRepository.findByStudentId(userPrincipal.getUser().getId()).stream()
                .map(GradeResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AttendanceResponse> saveAttendance(Long classId, AttendanceRequest request, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);

        List<Attendance> saved = new ArrayList<>();
        if (request.getRecords() != null) {
            for (AttendanceRecordItem item : request.getRecords()) {
                User student = userRepository.findById(item.getStudentId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                Attendance attendance = attendanceRepository
                        .findByClazzIdAndStudentIdAndAttendanceDate(classId, item.getStudentId(), request.getAttendanceDate())
                        .orElse(Attendance.builder().clazz(clazz).student(student).attendanceDate(request.getAttendanceDate()).build());
                attendance.setStatus(item.getStatus());
                saved.add(attendanceRepository.save(attendance));
            }
        }
        return saved.stream().map(AttendanceResponse::from).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceByDate(Long classId, LocalDate date, UserPrincipal userPrincipal) {
        return attendanceRepository.findByClazzIdAndAttendanceDate(classId, date).stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getMyAttendance(Long classId, UserPrincipal userPrincipal) {
        return attendanceRepository.findByClazzIdAndStudentId(classId, userPrincipal.getUser().getId()).stream()
                .map(AttendanceResponse::from)
                .collect(Collectors.toList());
    }
}
