package com.ex.learninghub.modules.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ex.learninghub.modules.admin.entity.MentorRequest;
import java.util.List;

@Repository
public interface MentorRequestRepository extends JpaRepository<MentorRequest, Long> {
    List<MentorRequest> findByStatus(String status);
    boolean existsByUserIdAndStatus(Long userId, String status);
}
