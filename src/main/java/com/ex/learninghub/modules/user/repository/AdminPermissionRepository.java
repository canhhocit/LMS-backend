package com.ex.learninghub.modules.user.repository;

import com.ex.learninghub.common.enums.AdminPermission;
import com.ex.learninghub.modules.user.entity.AdminPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminPermissionRepository extends JpaRepository<AdminPermissionEntity, Long> {
    Optional<AdminPermissionEntity> findByCode(AdminPermission code);

    List<AdminPermissionEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    boolean existsByUserIdAndPermissionCode(Long userId, String permissionCode);
}
