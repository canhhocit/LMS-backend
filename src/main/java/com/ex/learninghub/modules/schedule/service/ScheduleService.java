package com.ex.learninghub.modules.schedule.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.schedule.dto.request.ClassScheduleRequest;
import com.ex.learninghub.modules.schedule.dto.response.ScheduleResponse;

import java.util.List;

public interface ScheduleService {

    ScheduleResponse createSchedule(Long clazzId, ClassScheduleRequest request, UserPrincipal principal);

    ScheduleResponse updateSchedule(Long scheduleId, ClassScheduleRequest request, UserPrincipal principal);

    void deleteSchedule(Long scheduleId, UserPrincipal principal);

    List<ScheduleResponse> getSchedulesByClazz(Long clazzId, UserPrincipal principal);

    List<ScheduleResponse> getMyWeeklySchedule(UserPrincipal principal);
}
