package com.ex.learninghub.modules.grading.dto.response;

import com.ex.learninghub.common.enums.AttendanceStatus;
import com.ex.learninghub.modules.grading.entity.Attendance;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AttendanceResponse {

    private Long id;
    private Long classId;
    private Long studentId;
    private String studentName;
    private LocalDate attendanceDate;
    private AttendanceStatus status;

    public static AttendanceResponse from(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .classId(attendance.getClazz() != null ? attendance.getClazz().getId() : null)
                .studentId(attendance.getStudent() != null ? attendance.getStudent().getId() : null)
                .studentName(attendance.getStudent() != null ? attendance.getStudent().getFullName() : null)
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus())
                .build();
    }
}
