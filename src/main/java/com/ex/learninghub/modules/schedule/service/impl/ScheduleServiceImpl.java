package com.ex.learninghub.modules.schedule.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.ClassSchedule;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.ClassScheduleRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.schedule.dto.request.ClassScheduleRequest;
import com.ex.learninghub.modules.schedule.dto.response.ScheduleResponse;
import com.ex.learninghub.modules.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ClassScheduleRepository scheduleRepository;
    private final ClazzRepository clazzRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public ScheduleResponse createSchedule(Long clazzId, ClassScheduleRequest request, UserPrincipal principal) {
        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, principal);

        if (request.getEndPeriod() < request.getStartPeriod()) {
            throw new AppException(ErrorCode.SCHEDULE_CONFLICT);
        }

        // Conflict check within the same clazz
        boolean clazzConflict = scheduleRepository.findByClazzId(clazzId).stream()
                .anyMatch(s -> s.getDayOfWeek().equals(request.getDayOfWeek())
                        && periodsOverlap(s.getStartPeriod(), s.getEndPeriod(),
                                request.getStartPeriod(), request.getEndPeriod()));
        if (clazzConflict) {
            throw new AppException(ErrorCode.SCHEDULE_CONFLICT);
        }

        ClassSchedule schedule = ClassSchedule.builder()
                .clazzId(clazzId)
                .dayOfWeek(request.getDayOfWeek())
                .startPeriod(request.getStartPeriod())
                .endPeriod(request.getEndPeriod())
                .room(request.getRoom())
                .build();
        ClassSchedule saved = scheduleRepository.save(schedule);

        return ScheduleResponse.from(saved);
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ClassScheduleRequest request, UserPrincipal principal) {
        ClassSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        Clazz clazz = clazzRepository.findById(schedule.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, principal);

        if (request.getEndPeriod() < request.getStartPeriod()) {
            throw new AppException(ErrorCode.SCHEDULE_CONFLICT);
        }

        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartPeriod(request.getStartPeriod());
        schedule.setEndPeriod(request.getEndPeriod());
        schedule.setRoom(request.getRoom());
        scheduleRepository.save(schedule);

        return ScheduleResponse.from(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId, UserPrincipal principal) {
        ClassSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        Clazz clazz = clazzRepository.findById(schedule.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, principal);
        scheduleRepository.delete(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByClazz(Long clazzId, UserPrincipal principal) {
        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        Role role = principal.getUser().getRole();
        if (role == Role.STUDENT) {
            boolean enrolled = enrollmentRepository.existsByStudentIdAndClazzId(
                    principal.getUser().getId(), clazzId);
            if (!enrolled) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        } else if (role == Role.LECTURER
                && (clazz.getLecturer() == null
                || !clazz.getLecturer().getId().equals(principal.getUser().getId()))) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return scheduleRepository.findByClazzId(clazzId).stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getMyWeeklySchedule(UserPrincipal principal) {
        Long userId = principal.getUser().getId();
        Role role = principal.getUser().getRole();

        List<Long> clazzIds;
        if (role == Role.STUDENT) {
            clazzIds = enrollmentRepository.findByStudentId(userId).stream()
                    .map(e -> e.getClazz().getId())
                    .toList();
        } else if (role == Role.LECTURER) {
            clazzIds = clazzRepository.findByLecturerId(userId).stream()
                    .map(Clazz::getId)
                    .toList();
        } else { // ADMIN
            clazzIds = clazzRepository.findAll().stream().map(Clazz::getId).toList();
        }

        if (clazzIds.isEmpty()) {
            return List.of();
        }

        List<ClassSchedule> all = clazzIds.stream()
                .flatMap(id -> scheduleRepository.findByClazzId(id).stream())
                .toList();

        return all.stream()
                .sorted((a, b) -> {
                    int day = Integer.compare(a.getDayOfWeek(), b.getDayOfWeek());
                    return day != 0 ? day : Integer.compare(a.getStartPeriod(), b.getStartPeriod());
                })
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    private static boolean periodsOverlap(int s1, int e1, int s2, int e2) {
        return s1 <= e2 && s2 <= e1;
    }

    private void verifyLecturerOwnsClazz(Clazz clazz, UserPrincipal principal) {
        Role role = principal.getUser().getRole();
        if (role == Role.ADMIN) return;
        if (role == Role.LECTURER
                && clazz.getLecturer() != null
                && clazz.getLecturer().getId().equals(principal.getUser().getId())) {
            return;
        }
        throw new AppException(ErrorCode.FORBIDDEN);
    }

}
