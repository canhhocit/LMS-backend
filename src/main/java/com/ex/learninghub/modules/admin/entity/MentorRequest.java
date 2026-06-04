package com.ex.learninghub.modules.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import com.ex.learninghub.common.model.BaseEntity;
import com.ex.learninghub.modules.user.entity.User;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String experience;

    private String skills;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "rejection_reason")
    private String rejectionReason;
}
