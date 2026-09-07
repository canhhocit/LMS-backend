package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.ClazzPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClazzPermissionRepository extends JpaRepository<ClazzPermission, Long> {
    Optional<ClazzPermission> findByCode(String code);
}
