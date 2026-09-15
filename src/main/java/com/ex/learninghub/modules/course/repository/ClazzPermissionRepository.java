package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.ClazzPermission;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface ClazzPermissionRepository extends JpaRepository<ClazzPermission, Long> {
    Optional<ClazzPermission> findByCode(String code);
}
