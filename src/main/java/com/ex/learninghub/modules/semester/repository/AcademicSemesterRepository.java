package com.ex.learninghub.modules.semester.repository;

import com.ex.learninghub.modules.semester.entity.AcademicSemester;
import com.ex.learninghub.modules.semester.entity.SemesterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicSemesterRepository extends JpaRepository<AcademicSemester, Long> {

    Optional<AcademicSemester> findByCode(String code);

    List<AcademicSemester> findByStatus(SemesterStatus status);

    Optional<AcademicSemester> findFirstByStatus(SemesterStatus status);

    List<AcademicSemester> findByAcademicYearOrderBySemesterNoAsc(String academicYear);

    List<AcademicSemester> findAllByOrderByAcademicYearDescSemesterNoDesc();
}
