package com.ex.learninghub.modules.mentor.repository;

import com.ex.learninghub.common.enums.MentorRequestStatus;
import com.ex.learninghub.modules.mentor.entity.MentorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorRequestRepository extends JpaRepository<MentorRequest, Long> {
    
    Optional<MentorRequest> findByUserId(Long userId);
    
    List<MentorRequest> findByStatus(MentorRequestStatus status);
    
    Optional<MentorRequest> findByUserIdAndStatus(Long userId, MentorRequestStatus status);
    
    boolean existsByUserIdAndStatus(Long userId, MentorRequestStatus status);
}