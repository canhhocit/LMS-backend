package com.ex.learninghub.modules.tuition.repository;

import com.ex.learninghub.modules.tuition.entity.TuitionRate;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface TuitionRateRepository extends JpaRepository<TuitionRate, Long> {
    Optional<TuitionRate> findByAcademicYear(String academicYear);
    boolean existsByAcademicYear(String academicYear);
}
