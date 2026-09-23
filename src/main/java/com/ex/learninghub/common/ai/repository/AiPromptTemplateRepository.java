package com.ex.learninghub.common.ai.repository;

import com.ex.learninghub.common.ai.entity.AiPromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiPromptTemplateRepository extends JpaRepository<AiPromptTemplate, Long> {
    Optional<AiPromptTemplate> findByCode(String code);
    Optional<AiPromptTemplate> findByIsDefaultTrue();
}
