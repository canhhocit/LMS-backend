package com.ex.learninghub.common.ai.service.impl;

import com.ex.learninghub.common.ai.dto.UserAiPreferenceRequest;
import com.ex.learninghub.common.ai.dto.UserAiPreferenceResponse;
import com.ex.learninghub.common.ai.entity.UserAiPreference;
import com.ex.learninghub.common.ai.repository.UserAiPreferenceRepository;
import com.ex.learninghub.common.ai.service.UserAiPreferenceService;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAiPreferenceServiceImpl implements UserAiPreferenceService {

    private final UserAiPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserAiPreferenceResponse getMyAiPreference(UserPrincipal principal) {
        Long userId = principal.getUser().getId();
        UserAiPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserAiPreference.builder()
                        .user(userRepository.getReferenceById(userId))
                        .aiTone("FRIENDLY")
                        .responseLength("DETAILED")
                        .build());

        return mapToResponse(pref, userId);
    }

    @Override
    @Transactional
    public UserAiPreferenceResponse updateMyAiPreference(UserPrincipal principal, UserAiPreferenceRequest request) {
        Long userId = principal.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserAiPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserAiPreference.builder().user(user).build());

        if (request.getPreferredCallName() != null) {
            pref.setPreferredCallName(request.getPreferredCallName());
        }
        if (request.getAiTone() != null) {
            pref.setAiTone(request.getAiTone());
        }
        if (request.getCustomToneDescription() != null) {
            pref.setCustomToneDescription(request.getCustomToneDescription());
        }
        if (request.getResponseLength() != null) {
            pref.setResponseLength(request.getResponseLength());
        }
        if (request.getCustomContext() != null) {
            pref.setCustomContext(request.getCustomContext());
        }

        UserAiPreference saved = preferenceRepository.save(pref);
        log.info("Updated User AI Preferences for user ID {}", userId);
        return mapToResponse(saved, userId);
    }

    private UserAiPreferenceResponse mapToResponse(UserAiPreference pref, Long userId) {
        return UserAiPreferenceResponse.builder()
                .userId(userId)
                .preferredCallName(pref.getPreferredCallName())
                .aiTone(pref.getAiTone())
                .customToneDescription(pref.getCustomToneDescription())
                .responseLength(pref.getResponseLength())
                .customContext(pref.getCustomContext())
                .updatedAt(pref.getUpdatedAt())
                .build();
    }
}
