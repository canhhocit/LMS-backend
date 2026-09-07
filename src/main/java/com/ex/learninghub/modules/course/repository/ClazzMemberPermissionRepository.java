package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.ClazzMemberPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClazzMemberPermissionRepository extends JpaRepository<ClazzMemberPermission, Long> {
    List<ClazzMemberPermission> findByClazzIdAndUserId(Long clazzId, Long userId);
    boolean existsByClazzIdAndUserIdAndPermission_Code(Long clazzId, Long userId, String code);
    void deleteByClazzIdAndUserId(Long clazzId, Long userId);
}
