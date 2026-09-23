package com.ex.learninghub.common.ai.repository;

import com.ex.learninghub.common.ai.entity.UserAiPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAiPreferenceRepository extends JpaRepository<UserAiPreference, Long> {
    Optional<UserAiPreference> findByUserId(Long userId);
}
