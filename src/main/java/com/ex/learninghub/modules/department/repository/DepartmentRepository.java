package com.ex.learninghub.modules.department.repository;

import com.ex.learninghub.modules.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);
    boolean existsByCode(String code);
    List<Department> findByIsActiveTrue();
}
