package com.ex.learninghub.modules.tuition.repository;

import com.ex.learninghub.modules.tuition.entity.TuitionRate;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.time.LocalDate;


public interface TuitionRateRepository extends JpaRepository<TuitionRate, Long> {
    Optional<TuitionRate> findFirstByAcademicYearAndSemesterAndEffectiveFromLessThanEqualAndIsActiveTrueOrderByEffectiveFromDesc(
            String academicYear, String semester, LocalDate effectiveFrom);
    Optional<TuitionRate> findFirstByAcademicYearAndSemesterIsNullAndEffectiveFromLessThanEqualAndIsActiveTrueOrderByEffectiveFromDesc(
            String academicYear, LocalDate effectiveFrom);
    boolean existsByAcademicYearAndSemesterAndEffectiveFrom(String academicYear, String semester, LocalDate effectiveFrom);
    boolean existsByAcademicYearAndSemesterIsNullAndEffectiveFrom(String academicYear, LocalDate effectiveFrom);
    boolean existsByAcademicYearAndSemesterAndEffectiveFromAndIdNot(
            String academicYear, String semester, LocalDate effectiveFrom, Long id);
    boolean existsByAcademicYearAndSemesterIsNullAndEffectiveFromAndIdNot(
            String academicYear, LocalDate effectiveFrom, Long id);
}
