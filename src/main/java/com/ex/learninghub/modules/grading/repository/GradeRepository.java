package com.ex.learninghub.modules.grading.repository;

import com.ex.learninghub.modules.grading.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByClazzId(Long clazzId);
    List<Grade> findByStudentId(Long studentId);
    Optional<Grade> findByClazzIdAndStudentId(Long clazzId, Long studentId);

    /**
     * Lấy danh sách courseId mà sinh viên đã đạt (total_score >= ngưỡng).
     * Dùng để kiểm tra môn tiên quyết.
     */
    @Query("SELECT DISTINCT g.clazz.course.id FROM Grade g " +
           "WHERE g.student.id = :studentId AND g.totalScore >= :passScore")
    List<Long> findPassedCourseIds(@Param("studentId") Long studentId,
                                   @Param("passScore") BigDecimal passScore);
}
