package com.ex.learninghub.modules.schedule.service.impl;

import com.ex.learninghub.common.ai.AiClientService;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.entity.ClassSchedule;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClassScheduleRepository;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import com.ex.learninghub.modules.registration.repository.RegistrationPeriodRepository;
import com.ex.learninghub.modules.schedule.dto.request.AiScheduleRecommendRequest;
import com.ex.learninghub.modules.schedule.dto.response.AiScheduleOption;
import com.ex.learninghub.modules.schedule.dto.response.AiScheduleRecommendResponse;
import com.ex.learninghub.modules.schedule.dto.response.ScheduleResponse;
import com.ex.learninghub.modules.schedule.service.AiScheduleService;
import com.ex.learninghub.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiScheduleServiceImpl implements AiScheduleService {

    private final ClazzRepository clazzRepository;
    private final ClassScheduleRepository scheduleRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final AiClientService aiClientService;

    @Override
    @Transactional(readOnly = true)
    public AiScheduleRecommendResponse recommendSchedule(AiScheduleRecommendRequest request, UserPrincipal principal) {
        User user = principal != null ? principal.getUser() : null;
        String studentName = user != null ? user.getFullName() : "Sinh viên";

        // Determine semester & academicYear
        String semester = request.getSemester();
        String academicYear = request.getAcademicYear();
        if (semester == null || academicYear == null) {
            RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue().orElse(null);
            if (activePeriod != null) {
                if (semester == null) semester = activePeriod.getSemester();
                if (academicYear == null) academicYear = activePeriod.getAcademicYear();
            }
        }
        if (semester == null) semester = "1";
        if (academicYear == null) academicYear = "2024-2025";

        final String finalSem = semester;
        final String finalYear = academicYear;

        // Fetch open classes
        List<Clazz> allClasses = clazzRepository.findAll().stream()
                .filter(c -> finalSem.equals(c.getSemester()) && finalYear.equals(c.getAcademicYear()))
                .collect(Collectors.toList());

        if (allClasses.isEmpty()) {
            return AiScheduleRecommendResponse.builder()
                    .studentName(studentName)
                    .semester(finalSem)
                    .academicYear(finalYear)
                    .summaryAdvice("Hiện không có lớp học phần nào đang mở cho kỳ " + finalSem + " năm học " + finalYear + ".")
                    .options(Collections.emptyList())
                    .build();
        }

        // Fetch all schedules
        List<ClassSchedule> allSchedules = scheduleRepository.findAll();
        Map<Long, List<ClassSchedule>> schedulesByClazz = allSchedules.stream()
                .collect(Collectors.groupingBy(ClassSchedule::getClazzId));

        // Group classes by course
        Map<Long, List<Clazz>> classesByCourse = new HashMap<>();
        for (Clazz c : allClasses) {
            if (c.getCourse() == null) continue;
            // Check capacity
            long currentEnrolled = enrollmentRepository.countByClazzId(c.getId());
            if (c.getMaxStudents() != null && currentEnrolled >= c.getMaxStudents()) {
                continue; // Skip full classes
            }
            // Check if has schedules
            if (!schedulesByClazz.containsKey(c.getId()) || schedulesByClazz.get(c.getId()).isEmpty()) {
                continue; // Skip classes without schedules
            }
            classesByCourse.computeIfAbsent(c.getCourse().getId(), k -> new ArrayList<>()).add(c);
        }

        // Filter by desired courses if provided
        if (request.getDesiredCourseIds() != null && !request.getDesiredCourseIds().isEmpty()) {
            classesByCourse.keySet().retainAll(request.getDesiredCourseIds());
        }

        List<List<Clazz>> candidateCombinations = generateCombinations(new ArrayList<>(classesByCourse.values()), schedulesByClazz);

        if (candidateCombinations.isEmpty()) {
            // Fallback: relax constraints and pick non-overlapping single classes
            candidateCombinations = generateFallbackCombinations(allClasses, schedulesByClazz);
        }

        // Score combinations based on preferences
        List<AiScheduleOption> options = new ArrayList<>();
        int optIndex = 1;

        for (List<Clazz> combination : candidateCombinations.stream().limit(3).toList()) {
            double score = computeMatchScore(combination, schedulesByClazz, request);
            int totalCredits = combination.stream()
                    .mapToInt(c -> c.getCourse() != null && c.getCourse().getCredit() != null ? c.getCourse().getCredit() : 0)
                    .sum();

            List<ClazzResponse> clazzResponses = combination.stream()
                    .map(c -> ClazzResponse.from(c, enrollmentRepository.countByClazzId(c.getId())))
                    .toList();

            List<ScheduleResponse> scheduleResponses = combination.stream()
                    .flatMap(c -> schedulesByClazz.getOrDefault(c.getId(), Collections.emptyList()).stream())
                    .map(ScheduleResponse::from)
                    .toList();

            String title = switch (optIndex) {
                case 1 -> "Phương án Tối Ưu Tối Đa (Điểm khớp: " + Math.round(score) + "%)";
                case 2 -> "Phương án Tập Trung Ngày Học (Nghỉ ngày chọn)";
                default -> "Phương án Dân Lập / Tránh Tiết Sáng";
            };

            String reasoning = generateReasoning(optIndex, combination, schedulesByClazz, request);

            options.add(AiScheduleOption.builder()
                    .optionId("OPTION_" + optIndex)
                    .title(title)
                    .totalCredits(totalCredits)
                    .matchScore(score)
                    .reasoning(reasoning)
                    .suggestedClasses(clazzResponses)
                    .scheduleDetails(scheduleResponses)
                    .build());

            optIndex++;
        }

        String advice = "AI đã phân tích " + allClasses.size() + " lớp học phần khả dụng và xây dựng " + options.size()
                + " phương án thời khóa biểu không trùng lịch, tối ưu theo nguyện vọng nghỉ ngơi và trần tín chỉ của bạn.";

        if (aiClientService.isAiConfigured() && user != null) {
            try {
                String aiSummary = callAiSummaryPrompt(studentName, request, options);
                if (aiSummary != null && !aiSummary.isBlank()) {
                    advice = aiSummary;
                }
            } catch (Exception e) {
                log.warn("Không thể gọi AI Client cho Schedule Advice, dùng nhận xét quy tắc: {}", e.getMessage());
            }
        }

        return AiScheduleRecommendResponse.builder()
                .studentName(studentName)
                .semester(finalSem)
                .academicYear(finalYear)
                .summaryAdvice(advice)
                .options(options)
                .build();
    }

    private List<List<Clazz>> generateCombinations(List<List<Clazz>> coursesClazzes, Map<Long, List<ClassSchedule>> schedulesByClazz) {
        List<List<Clazz>> result = new ArrayList<>();
        backtrackCombination(coursesClazzes, 0, new ArrayList<>(), result, schedulesByClazz);
        // Sort by larger combination size
        result.sort((a, b) -> Integer.compare(b.size(), a.size()));
        return result;
    }

    private void backtrackCombination(List<List<Clazz>> coursesClazzes, int depth, List<Clazz> current, List<List<Clazz>> result, Map<Long, List<ClassSchedule>> schedulesByClazz) {
        if (result.size() >= 10) return;
        if (depth == coursesClazzes.size()) {
            if (!current.isEmpty()) result.add(new ArrayList<>(current));
            return;
        }

        for (Clazz clazz : coursesClazzes.get(depth)) {
            if (isNoConflict(current, clazz, schedulesByClazz)) {
                current.add(clazz);
                backtrackCombination(coursesClazzes, depth + 1, current, result, schedulesByClazz);
                current.remove(current.size() - 1);
            }
        }
        // Also allow skipping this course if no non-overlapping class found
        backtrackCombination(coursesClazzes, depth + 1, current, result, schedulesByClazz);
    }

    private boolean isNoConflict(List<Clazz> currentList, Clazz newClazz, Map<Long, List<ClassSchedule>> schedulesByClazz) {
        List<ClassSchedule> newSchedules = schedulesByClazz.getOrDefault(newClazz.getId(), Collections.emptyList());
        for (Clazz c : currentList) {
            List<ClassSchedule> existing = schedulesByClazz.getOrDefault(c.getId(), Collections.emptyList());
            for (ClassSchedule s1 : existing) {
                for (ClassSchedule s2 : newSchedules) {
                    if (Objects.equals(s1.getDayOfWeek(), s2.getDayOfWeek())) {
                        boolean overlap = Math.max(s1.getStartPeriod(), s2.getStartPeriod()) <= Math.min(s1.getEndPeriod(), s2.getEndPeriod());
                        if (overlap) return false;
                    }
                }
            }
        }
        return true;
    }

    private List<List<Clazz>> generateFallbackCombinations(List<Clazz> allClasses, Map<Long, List<ClassSchedule>> schedulesByClazz) {
        List<List<Clazz>> result = new ArrayList<>();
        List<Clazz> valid = allClasses.stream()
                .filter(c -> schedulesByClazz.containsKey(c.getId()) && !schedulesByClazz.get(c.getId()).isEmpty())
                .limit(5)
                .toList();

        List<Clazz> nonConflicting = new ArrayList<>();
        for (Clazz c : valid) {
            if (isNoConflict(nonConflicting, c, schedulesByClazz)) {
                nonConflicting.add(c);
            }
        }
        if (!nonConflicting.isEmpty()) {
            result.add(nonConflicting);
        }
        return result;
    }

    private double computeMatchScore(List<Clazz> combination, Map<Long, List<ClassSchedule>> schedulesByClazz, AiScheduleRecommendRequest request) {
        double score = 100.0;
        List<ClassSchedule> schedules = combination.stream()
                .flatMap(c -> schedulesByClazz.getOrDefault(c.getId(), Collections.emptyList()).stream())
                .toList();

        if (request.getPreferOffDays() != null && !request.getPreferOffDays().isEmpty()) {
            for (ClassSchedule s : schedules) {
                if (request.getPreferOffDays().contains(s.getDayOfWeek())) {
                    score -= 15.0; // Penalty if studying on preferred off day
                }
            }
        }

        if (Boolean.TRUE.equals(request.getAvoidEarlyMorning())) {
            for (ClassSchedule s : schedules) {
                if (s.getStartPeriod() <= 2) {
                    score -= 10.0; // Penalty for early morning period 1-2
                }
            }
        }

        return Math.max(50.0, Math.min(99.0, score));
    }

    private String generateReasoning(int optIndex, List<Clazz> combination, Map<Long, List<ClassSchedule>> schedulesByClazz, AiScheduleRecommendRequest request) {
        Set<Integer> studyDays = combination.stream()
                .flatMap(c -> schedulesByClazz.getOrDefault(c.getId(), Collections.emptyList()).stream())
                .map(ClassSchedule::getDayOfWeek)
                .collect(Collectors.toSet());

        String dayStr = studyDays.stream().sorted()
                .map(d -> d == 7 ? "Chủ Nhật" : "Thứ " + d)
                .collect(Collectors.joining(", "));

        return String.format("Phương án này xếp %d lớp học phần vào các ngày (%s). %s.",
                combination.size(),
                dayStr,
                studyDays.size() <= 4 ? "Lịch học tập trung gọn gàng, có nhiều ngày trống tự học hoặc làm thêm" : "Lịch học rải đều giúp không bị quá tải trong một ngày");
    }

    private String callAiSummaryPrompt(String studentName, AiScheduleRecommendRequest request, List<AiScheduleOption> options) throws Exception {
        String prompt = String.format("""
            Bạn là Trợ lý AI xếp lịch học thông minh dành cho sinh viên %s.
            Hệ thống vừa đề xuất %d phương án thời khóa biểu tối ưu.
            Nguyện vọng sinh viên: %s.
            Hãy đưa ra 1 đoạn tổng kết ngắn (3-4 câu) khuyến nghị phương án tốt nhất cho sinh viên.
            """, studentName, options.size(), request.getCustomPreference() != null ? request.getCustomPreference() : "Tối ưu lịch học không bị trùng");

        return aiClientService.generateContent("Bạn là Trợ lý AI Cố vấn Đào tạo.", prompt);
    }
}
