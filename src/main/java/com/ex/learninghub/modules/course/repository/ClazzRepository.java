package com.ex.learninghub.modules.course.repository;

import com.ex.learninghub.modules.course.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClazzRepository extends JpaRepository<Clazz, Long> {
    Optional<Clazz> findByClassCode(String classCode);
    List<Clazz> findByLecturerId(Long lecturerId);
    List<Clazz> findByCourseId(Long courseId);
    boolean existsByClassCode(String classCode);
}
