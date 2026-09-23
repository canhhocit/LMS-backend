package com.ex.learninghub.common.ai.service;

import com.ex.learninghub.modules.user.entity.User;

public interface PersonalizationEngine {
    String buildPersonalizedSystemPrompt(User student, String templateCode, String lecturerTone);
}
