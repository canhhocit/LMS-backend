package com.ex.learninghub.modules.user.repository;

import com.ex.learninghub.common.enums.AdminPermission;
import com.ex.learninghub.modules.user.entity.AdminPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminPermissionRepository extends JpaRepository<AdminPermissionEntity, Long> {
    Optional<AdminPermissionEntity> findByCode(AdminPermission code);
}
