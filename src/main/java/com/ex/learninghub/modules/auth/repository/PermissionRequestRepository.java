package com.ex.learninghub.modules.auth.repository;

import com.ex.learninghub.modules.auth.entity.PermissionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PermissionRequestRepository extends JpaRepository<PermissionRequest, Long> {

    List<PermissionRequest> findByLecturerIdOrderByCreatedAtDesc(Long lecturerId);

    List<PermissionRequest> findByStatus(String status);

    @Query("SELECT pr FROM PermissionRequest pr WHERE pr.lecturer.id = :lecturerId AND pr.classId = :classId AND pr.permissionType = :type AND pr.status = 'APPROVED' AND (pr.validUntil IS NULL OR pr.validUntil > :now)")
    Optional<PermissionRequest> findActiveGrant(
            @Param("lecturerId") Long lecturerId,
            @Param("classId") Long classId,
            @Param("type") String type,
            @Param("now") LocalDateTime now
    );

    List<PermissionRequest> findByStatusAndValidUntilBefore(String status, LocalDateTime now);
}
