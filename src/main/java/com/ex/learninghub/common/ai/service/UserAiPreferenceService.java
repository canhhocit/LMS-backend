package com.ex.learninghub.common.ai.service;

import com.ex.learninghub.common.ai.dto.UserAiPreferenceRequest;
import com.ex.learninghub.common.ai.dto.UserAiPreferenceResponse;
import com.ex.learninghub.common.security.UserPrincipal;

public interface UserAiPreferenceService {
    UserAiPreferenceResponse getMyAiPreference(UserPrincipal principal);
    UserAiPreferenceResponse updateMyAiPreference(UserPrincipal principal, UserAiPreferenceRequest request);
}
