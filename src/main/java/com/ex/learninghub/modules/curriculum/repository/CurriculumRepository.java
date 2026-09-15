package com.ex.learninghub.modules.curriculum.repository;

import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByIsActiveTrue();
}
