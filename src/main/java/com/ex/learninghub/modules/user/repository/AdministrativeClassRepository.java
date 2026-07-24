package com.ex.learninghub.modules.user.repository;

import com.ex.learninghub.modules.user.entity.AdministrativeClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdministrativeClassRepository extends JpaRepository<AdministrativeClass, Long> {
    Optional<AdministrativeClass> findByClassName(String className);
    boolean existsByClassName(String className);
    List<AdministrativeClass> findByFaculty(String faculty);
}
