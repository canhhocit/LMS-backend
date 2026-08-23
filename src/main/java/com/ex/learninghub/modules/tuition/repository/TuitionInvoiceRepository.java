package com.ex.learninghub.modules.tuition.repository;

import com.ex.learninghub.modules.tuition.entity.TuitionInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuitionInvoiceRepository extends JpaRepository<TuitionInvoice, Long> {
    List<TuitionInvoice> findByStudentId(Long studentId);
    Optional<TuitionInvoice> findByStudentIdAndSemesterAndAcademicYear(Long studentId, String semester, String academicYear);
    List<TuitionInvoice> findBySemesterAndAcademicYear(String semester, String academicYear);
}
