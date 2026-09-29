package com.ex.learninghub.modules.schedule.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.schedule.dto.request.AiScheduleRecommendRequest;
import com.ex.learninghub.modules.schedule.dto.response.AiScheduleRecommendResponse;

public interface AiScheduleService {

    /**
     * AI phân tích và gợi ý top các thời khóa biểu tối ưu dựa theo nguyện vọng sinh viên.
     */
    AiScheduleRecommendResponse recommendSchedule(AiScheduleRecommendRequest request, UserPrincipal principal);
}
