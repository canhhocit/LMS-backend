package com.ex.learninghub.modules.grading.repository;

import com.ex.learninghub.modules.grading.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByClazzId(Long clazzId);
    List<Attendance> findByStudentId(Long studentId);
    Optional<Attendance> findByClazzIdAndStudentIdAndAttendanceDate(Long clazzId, Long studentId, LocalDate attendanceDate);
    List<Attendance> findByClazzIdAndAttendanceDate(Long clazzId, LocalDate attendanceDate);
    List<Attendance> findByClazzIdAndStudentId(Long clazzId, Long studentId);

    long countByClazzIdAndStudentId(Long clazzId, Long studentId);

    long countByClazzIdAndStudentIdAndStatus(Long clazzId, Long studentId, com.ex.learninghub.common.enums.AttendanceStatus status);
}
