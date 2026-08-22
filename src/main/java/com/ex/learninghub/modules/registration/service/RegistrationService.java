package com.ex.learninghub.modules.registration.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.registration.dto.request.RegistrationPeriodRequest;
import com.ex.learninghub.modules.registration.dto.response.RegistrationPeriodResponse;
import com.ex.learninghub.modules.registration.dto.response.RegistrationResponse;

import java.util.List;

public interface RegistrationService {

    // ADMIN: CRUD registration period
    RegistrationPeriodResponse createPeriod(RegistrationPeriodRequest request);
    RegistrationPeriodResponse updatePeriod(Long id, RegistrationPeriodRequest request);
    void deletePeriod(Long id);
    List<RegistrationPeriodResponse> listPeriods();
    RegistrationPeriodResponse getActivePeriod();

    // STUDENT: tự đăng ký / hủy
    RegistrationResponse register(Long clazzId, UserPrincipal principal);
    void unregister(Long clazzId, UserPrincipal principal);

    // STUDENT: xem lớp đã đăng ký trong đợt hiện tại
    List<RegistrationResponse> getMyRegistrations(UserPrincipal principal);
}
