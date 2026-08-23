package com.ex.learninghub.modules.tuition.repository;

import com.ex.learninghub.modules.tuition.entity.TuitionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TuitionRateRepository extends JpaRepository<TuitionRate, Long> {
    Optional<TuitionRate> findByAcademicYear(String academicYear);
    boolean existsByAcademicYear(String academicYear);
}
