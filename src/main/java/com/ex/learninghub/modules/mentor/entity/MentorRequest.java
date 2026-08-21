package com.ex.learninghub.modules.mentor.entity;

import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.common.enums.MentorRequestStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mentor_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorRequest extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "experience", columnDefinition = "TEXT")
    private String experience;
    
    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MentorRequestStatus status;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
}