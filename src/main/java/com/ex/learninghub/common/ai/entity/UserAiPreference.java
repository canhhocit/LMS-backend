package com.ex.learninghub.common.ai.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_ai_preferences")
public class UserAiPreference extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "preferred_call_name", length = 100)
    private String preferredCallName;

    @Column(name = "ai_tone", length = 100)
    @Builder.Default
    private String aiTone = "FRIENDLY";

    @Column(name = "custom_tone_description", columnDefinition = "TEXT")
    private String customToneDescription;

    @Column(name = "response_length", length = 50)
    @Builder.Default
    private String responseLength = "DETAILED";

    @Column(name = "custom_context", columnDefinition = "TEXT")
    private String customContext;
}
