package com.ex.learninghub.modules.grading.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.grading.dto.request.AttendanceRequest;
import com.ex.learninghub.modules.grading.dto.request.GradeRequest;
import com.ex.learninghub.modules.grading.dto.response.AttendanceResponse;
import com.ex.learninghub.modules.grading.dto.response.GradeResponse;

import java.time.LocalDate;
import java.util.List;

public interface GradingService {

    GradeResponse upsertGrade(Long classId, GradeRequest request, UserPrincipal userPrincipal);

    List<GradeResponse> getGradesByClass(Long classId, UserPrincipal userPrincipal);

    List<GradeResponse> getMyGrades(UserPrincipal userPrincipal);

    List<AttendanceResponse> saveAttendance(Long classId, AttendanceRequest request, UserPrincipal userPrincipal);

    List<AttendanceResponse> getAttendanceByDate(Long classId, LocalDate date, UserPrincipal userPrincipal);

    List<AttendanceResponse> getMyAttendance(Long classId, UserPrincipal userPrincipal);

    List<com.ex.learninghub.modules.grading.dto.response.TranscriptResponse> getMyTranscript(UserPrincipal userPrincipal);
}
