package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.ClazzMemberPermission;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface ClazzMemberPermissionRepository extends JpaRepository<ClazzMemberPermission, Long> {
    List<ClazzMemberPermission> findByClazzIdAndUserId(Long clazzId, Long userId);
    boolean existsByClazzIdAndUserIdAndPermission_Code(Long clazzId, Long userId, String code);
    void deleteByClazzIdAndUserId(Long clazzId, Long userId);
}
